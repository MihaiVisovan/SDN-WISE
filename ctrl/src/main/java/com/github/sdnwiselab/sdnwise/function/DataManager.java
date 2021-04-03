package com.github.sdnwiselab.sdnwise.function;

import com.github.sdnwiselab.sdnwise.flowtable.FlowTableEntry;
import com.github.sdnwiselab.sdnwise.mote.core.MoteCore;
import com.github.sdnwiselab.sdnwise.mote.standalone.Mote;
import com.github.sdnwiselab.sdnwise.packet.DataPacket;
import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import com.github.sdnwiselab.sdnwise.util.Neighbor;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;

import java.nio.charset.Charset;
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
        LocalDateTime dateTimeOne = LocalDateTime.parse("2004-03-13T23:45:24.22222");
        LocalDateTime dateTimeTwo = LocalDateTime.parse("2004-03-13T23:47:12.34567");
        DataPacket dp = (DataPacket) np;
        MoteCore moteCore = (MoteCore) object;
        List<Object> data1 = moteCore.getDataByNumber("temperature", 10);
        List<Object> data2 = moteCore.getDataWithinDates("temperature", dateTimeOne, dateTimeTwo);

        System.out.println(data1);
        System.out.println(data2);

        // dp.setPayload(adcRegister.getBytes(Charset.forName("UTF-8")));
        // controller.sendNetworkPacket(dp);
    }
}
