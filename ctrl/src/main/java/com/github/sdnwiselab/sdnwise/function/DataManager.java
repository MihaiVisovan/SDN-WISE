package com.github.sdnwiselab.sdnwise.function;

import com.github.sdnwiselab.sdnwise.controller.AbstractController;
import com.github.sdnwiselab.sdnwise.flowtable.FlowTableEntry;
import com.github.sdnwiselab.sdnwise.mote.core.MoteCore;
import com.github.sdnwiselab.sdnwise.packet.DataPacket;
import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import com.github.sdnwiselab.sdnwise.util.Neighbor;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Sebastiano Milardo
 */
public class DataManager implements FunctionInterface {

    public AbstractController controller;

    @Override
    public final void function(
        final Object object,
        final HashMap<String, List<Object>> adcRegister,
        final List<FlowTableEntry> flowTable,
        final Set<Neighbor> neighborTable,
        final ArrayList<Integer> statusRegister,
        final List<NodeAddress> acceptedId,
        final ArrayBlockingQueue<NetworkPacket> flowTableQueue,
        final ArrayBlockingQueue<NetworkPacket> txQueue,
        final byte[] args,
        final NetworkPacket np
    ) {
        // get some particular data based on user input
        LocalDateTime dateTimeOne = LocalDateTime.parse("2004-01-13T23:45:24.22222");
        LocalDateTime dateTimeTwo = LocalDateTime.parse("2004-05-13T23:47:12.34567");
        MoteCore moteCore = (MoteCore) object;
        List<Object> dataWithinDates = moteCore.getDataWithinDates("temperature", dateTimeOne, dateTimeTwo);

        System.out.println("Data manager calllled");
        
        // send the data to sink and then to the controller
        sendData(dataWithinDates.subList(0, 1), np);
    }

    public final void sendData(List<Object> data, NetworkPacket np) {

        data.forEach(x -> {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(bos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                oos.writeObject(x);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] payload = bos.toByteArray();
    
            DataPacket dp = new DataPacket(np.getNet(), np.getSrc(), np.getDst(), payload);

            

            // dp.setNxh(np.getSrc());
            // controller.sendNetworkPacket(dp);
        });
    }
}
