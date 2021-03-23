/*
 * Copyright (C) 2015 SDN-WISE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.sdnwiselab.sdnwise.mote.core;

import com.github.sdnwiselab.sdnwise.flowtable.FlowTableEntry;
import static com.github.sdnwiselab.sdnwise.flowtable.FlowTableInterface.CONST;
import static com.github.sdnwiselab.sdnwise.flowtable.FlowTableInterface.PACKET;
import com.github.sdnwiselab.sdnwise.flowtable.ForwardUnicastAction;
import com.github.sdnwiselab.sdnwise.flowtable.Window;
import static com.github.sdnwiselab.sdnwise.flowtable.Window.EQUAL;
import static com.github.sdnwiselab.sdnwise.flowtable.Window.W_SIZE_2;
import static com.github.sdnwiselab.sdnwise.flowtable.Window.fromString;
import com.github.sdnwiselab.sdnwise.mote.battery.Dischargeable;
import com.github.sdnwiselab.sdnwise.packet.BeaconPacket;
import com.github.sdnwiselab.sdnwise.packet.ConfigPacket;
import com.github.sdnwiselab.sdnwise.packet.DataPacket;
import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import static com.github.sdnwiselab.sdnwise.packet.NetworkPacket.DFLT_TTL_MAX;
import static com.github.sdnwiselab.sdnwise.packet.NetworkPacket.DST_INDEX;
import com.github.sdnwiselab.sdnwise.util.Neighbor;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import com.github.sdnwiselab.sdnwise.mote.standalone.SensorData;


import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

import javax.swing.text.StyledEditorKit.BoldAction;

/**
 * @author Sebastiano Milardo
 */
public class MoteCore extends AbstractCore {

    /**
     * Sensor measurements
     */
    private final static HashMap<String, List<Object>> sensors = new HashMap<String, List<Object>>();

    /**
     * Mote index
     */
    private int index;

    /**
     * Creates the core of a mote.
     * @param sensors the measurements of the mote
     * @param net the network id of the mote
     * @param na the node address of the node
     * @param battery the battery of the node
     */
    public MoteCore(final byte net, final NodeAddress na,
            final Dischargeable battery, final int index) {
        super(sensors, net, na, battery);
        this.index = index;
        addMeasurementKeys();
        new Thread(new ReadSensorData()).start();
    }

    private class ReadSensorData implements Runnable {
        @Override
        public void run() {
            File myObj = null;
            Scanner myReader = null;
            try {
                myObj = new File("/home/mihai/sdn-wise-java/data/src/main/resources/sensor_data_" + index + ".txt");
                myReader = new Scanner(myObj);
                
              } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
              }
              
            while (myReader.hasNextLine()) 
            {
                String data = myReader.nextLine();
                String[] splittedData = data.split("\t");
                String date = splittedData[0];
                String time = splittedData[1];
                String epoch = splittedData[2];
                String temperature = splittedData[4];
                String humidity = splittedData[5];
                String light = splittedData[6];
                String voltage = splittedData[7];

                Object o1 = new SensorData("temperature", temperature, date, time, epoch);
                Object o2 = new SensorData("humidity", humidity, date, time, epoch);
                Object o3 = new SensorData("light", light, date, time, epoch);
                Object o4 = new SensorData("voltage", voltage, date, time, epoch);

            
                sensors.get("temperature").add(o1);
                sensors.get("humidity").add(o2);
                sensors.get("light").add(o3);
                sensors.get("voltage").add(o4);

                // System.out.println("Index: " + index + " measured values: " + sensors.get("temperature") + '\n');

                try {
                    Thread.sleep(31000);
                } 
                catch (InterruptedException e) 
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            myReader.close();
        }
    }

    public final void addMeasurementKeys(){
        this.sensors.put("temperature", new ArrayList());
        this.sensors.put("humidity", new ArrayList());
        this.sensors.put("light", new ArrayList());
        this.sensors.put("voltage", new ArrayList());
    }

    @Override
    public final void controllerTX(final NetworkPacket np) {
        np.setNxh(getNextHopVsSink());
        radioTX(np);
    }

    @Override
    public final void dataCallback(final DataPacket dp) {
        if (getFunctions().get(1) == null) {
            log(Level.INFO, new String(dp.getData(),
                    Charset.forName("UTF-8")));
            dp.setSrc(getMyAddress())
                    .setDst(getActualSinkAddress())
                    .setTtl((byte) getRuleTtl());
            runFlowMatch(dp);
        } else {
            getFunctions().get(1).function(getSensors(),
                    getFlowTable(),
                    getNeighborTable(),
                    getStatusRegister(),
                    getAcceptedId(),
                    getFtQueue(),
                    getTxQueue(),
                    new byte[0],
                    dp);
        }
    }

    @Override
    protected final void rxBeacon(final BeaconPacket bp, final int rssi) {
        if (rssi > getRssiMin()) {
            if (bp.getDistance() < getSinkDistance()
                    && (rssi > getSinkRssi())) {
                setActive(true);
                FlowTableEntry toSink = new FlowTableEntry();
                toSink.addWindow(new Window()
                        .setOperator(EQUAL)
                        .setSize(W_SIZE_2)
                        .setLhsLocation(PACKET)
                        .setLhs(DST_INDEX)
                        .setRhsLocation(CONST)
                        .setRhs(bp.getSinkAddress().intValue()));
                toSink.addWindow(fromString("P.TYP == 3"));
                toSink.addAction(new ForwardUnicastAction(bp.getSrc()));
                getFlowTable().set(0, toSink);

                setSinkDistance(bp.getDistance() + 1);
                setSinkRssi(rssi);
            } else if ((bp.getDistance() + 1) == getSinkDistance()
                    && getNextHopVsSink().equals(bp.getSrc())) {
                getFlowTable().get(0).getStats().restoreTtl();
                getFlowTable().get(0).getWindows().get(0)
                        .setRhs(bp.getSinkAddress().intValue());
            }
            Neighbor nb = new Neighbor(bp.getSrc(), rssi, bp.getBattery());
            getNeighborTable().add(nb);
        }
    }

    @Override
    protected final void rxConfig(final ConfigPacket cp) {
        NodeAddress dest = cp.getDst();
        if (!dest.equals(getMyAddress())) {
            runFlowMatch(cp);
        } else if (execConfigPacket(cp)) {
            cp.setSrc(getMyAddress());
            cp.setDst(getActualSinkAddress());
            cp.setTtl((byte) getRuleTtl());
            runFlowMatch(cp);
        }
    }

    @Override
    protected final NodeAddress getActualSinkAddress() {
        return new NodeAddress(getFlowTable().get(0).getWindows()
                .get(0).getRhs());
    }

    @Override
    protected final void initSdnWiseSpecific() {
        reset();
    }

    @Override
    protected final void reset() {
        setSinkDistance(DFLT_TTL_MAX + 1);
        setSinkRssi(0);
        setActive(false);
    }
}
