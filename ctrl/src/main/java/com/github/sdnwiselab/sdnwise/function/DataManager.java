package com.github.sdnwiselab.sdnwise.function;

import com.github.sdnwiselab.sdnwise.flowtable.FlowTableEntry;
import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import com.github.sdnwiselab.sdnwise.util.Neighbor;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
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
        System.out.println("Sensor data" + adcRegister + "\n");
    }
}
