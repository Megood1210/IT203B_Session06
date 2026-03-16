import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Ticket {
    String ticketId;
    boolean isHeld = false;
    boolean isSold = false;
    boolean isVIP = false;
    long holdExpiryTime = 0;

    public Ticket(String id) {
        this.ticketId = id;
    }
}

class TicketPool {
    String roomName;
    List<Ticket> tickets = new ArrayList<>();

    public TicketPool(String roomName, int capacity) {
        this.roomName = roomName;

        for (int i = 1; i <= capacity; i++) {
            tickets.add(new Ticket(roomName + "-" + String.format("%03d", i)));
        }
    }

    public synchronized Ticket holdTicket(boolean isVIP) {
        for (Ticket t : tickets) {
            if (!t.isSold && !t.isHeld) {

                t.isHeld = true;
                t.isVIP = isVIP;
                t.holdExpiryTime = System.currentTimeMillis() + 5000;

                return t;
            }
        }

        return null;
    }

    public synchronized boolean sellHeldTicket(Ticket t) {
        if (t != null && t.isHeld && !t.isSold) {
            t.isSold = true;
            t.isHeld = false;

            return true;
        }

        return false;
    }

    public synchronized void releaseExpiredTickets() {
        long now = System.currentTimeMillis();
        for (Ticket t : tickets) {
            if (t.isHeld && !t.isSold && now > t.holdExpiryTime) {
                System.out.println("TimeoutManager: Vé " + t.ticketId + " hết hạn giữ, đã trả lại kho");

                t.isHeld = false;
            }
        }
    }
}

class BookingCounter implements Runnable {
    String name;
    TicketPool pool;
    boolean isVIP;
    int delayBeforePay;

    public BookingCounter(String name, TicketPool pool, boolean vip, int delay) {
        this.name = name;
        this.pool = pool;
        this.isVIP = vip;
        this.delayBeforePay = delay;
    }

    @Override
    public void run() {
        try {
            Ticket t = pool.holdTicket(isVIP);

            if (t == null) {

                System.out.println(name + ": Vé đang được giữ bởi quầy khác, chờ...");
                return;
            }

            System.out.println(name + ": Đã giữ vé " + t.ticketId + (isVIP ? " (VIP)" : "") +". Vui lòng thanh toán trong 5s");

            Thread.sleep(delayBeforePay);

            boolean ok = pool.sellHeldTicket(t);

            if (ok) {
                System.out.println(name +": Thanh toán thành công vé " + t.ticketId);
            }
        } catch (Exception e) {}
    }
}
class TimeoutManager implements Runnable {
    List<TicketPool> pools;

    public TimeoutManager(List<TicketPool> pools) {
        this.pools = pools;
    }

    @Override
    public void run() {
        while (true) {

            try {
                Thread.sleep(1000);
            } catch (Exception e) {}

            for (TicketPool pool : pools) {
                pool.releaseExpiredTickets();
            }
        }
    }
}


public class Bai5 {
    public static void main(String[] args) throws Exception {
        TicketPool roomA = new TicketPool("A", 2);

        List<TicketPool> pools = Arrays.asList(roomA);

        Thread timeout = new Thread(new TimeoutManager(pools));
        timeout.start();

        Thread q1 = new Thread(new BookingCounter("Quầy 1", roomA, true, 3000));
        q1.start();

        Thread.sleep(1000);

        Thread q2 = new Thread(new BookingCounter("Quầy 2", roomA, false, 8000));
        q2.start();

        Thread.sleep(2500);

        Thread q3 = new Thread(new BookingCounter("Quầy 3", roomA, false, 3000));
        q3.start();
    }
}