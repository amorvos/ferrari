package com.cip.ferrari.commons;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalHost {

    private static final Logger logger = LoggerFactory.getLogger(LocalHost.class);

    private LocalHost() {
    }

    private static final String LOCA_LHOST = "127.0.0.1";

    private static final String ANY_HOST = "0.0.0.0";

    private static class LocalHostHolder {

        private static final List<InetAddress> all;

        private static final List<Inet4Address> loopV4;
        private static final List<Inet6Address> loopV6;

        private static final List<Inet4Address> noLoopV4;
        private static final List<Inet6Address> noLoopV6;

        static {
            ArrayList<InetAddress> _all = new ArrayList<>();

            ArrayList<Inet4Address> _loopV4 = new ArrayList<>();
            ArrayList<Inet6Address> _loopV6 = new ArrayList<>();

            ArrayList<Inet4Address> _noLoopV4 = new ArrayList<>();
            ArrayList<Inet6Address> _noLoopV6 = new ArrayList<>();

            try {
                for (Enumeration<NetworkInterface> em = NetworkInterface.getNetworkInterfaces(); em
                        .hasMoreElements();) {
                    for (InterfaceAddress ia : em.nextElement().getInterfaceAddresses()) {

                        InetAddress addr = ia.getAddress();
                        _all.add(addr);

                        if (addr.isLoopbackAddress()) {
                            if (addr instanceof Inet4Address)
                                _loopV4.add((Inet4Address) addr);
                            else
                                _loopV6.add((Inet6Address) addr);
                        } else {
                            if (addr instanceof Inet4Address)
                                _noLoopV4.add((Inet4Address) addr);
                            else
                                _noLoopV6.add((Inet6Address) addr);
                        }
                    }
                }

            } catch (SocketException e) {
                e.printStackTrace();
            }

            all = Collections.unmodifiableList(_all);
            loopV4 = Collections.unmodifiableList(_loopV4);
            loopV6 = Collections.unmodifiableList(_loopV6);
            noLoopV4 = Collections.unmodifiableList(_noLoopV4);
            noLoopV6 = Collections.unmodifiableList(_noLoopV6);
        }

    }

    private static String hostName = null;

    public static String getHostName() {
        if (hostName != null)
            return hostName;
        String id = "LocalHost";
        try {
            id = InetAddress.getLocalHost().getHostName();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return hostName = id;
    }

    public static List<InetAddress> getAll() {
        return LocalHostHolder.all;
    }

    public static List<Inet4Address> getLoopbackV4() {
        return LocalHostHolder.loopV4;
    }

    public static List<Inet6Address> getLoopbackV6() {
        return LocalHostHolder.loopV6;
    }

    public static List<Inet4Address> getNoLoopBackV4() {
        return LocalHostHolder.noLoopV4;
    }

    public static List<Inet6Address> getNoLoopBackV6() {
        return LocalHostHolder.noLoopV6;
    }

    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$");

    public static boolean isValidAddress(String address) {
        return ADDRESS_PATTERN.matcher(address).matches();
    }

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress())
            return false;
        String name = address.getHostAddress();
        return (name != null && !ANY_HOST.equals(name) && !LOCA_LHOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            try {
                                InetAddress address = addresses.nextElement();
                                if (isValidAddress(address)) {
                                    return address;
                                }
                            } catch (Throwable e) {
                                logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                            }
                        }
                    } catch (Throwable e) {
                        logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    private static volatile InetAddress LOCAL_ADDRESS = null;

    /**
     * 遍历本地网卡，返回第一个合理的IP。
     *
     * @return 本地网卡IP
     */
    private static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    public static String getLocalHost() {
        final InetAddress address = getLocalAddress();
        return address == null ? LOCA_LHOST : address.getHostAddress();
    }

    public static void main(String[] args) {
        System.out.println(getNoLoopBackV4().get(0).getHostAddress());
    }
}
