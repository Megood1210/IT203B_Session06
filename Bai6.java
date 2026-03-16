import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Ticket1 {
    String id;
    boolean sold = false;

    public Ticket1(String id) {
        this.id = id;
    }
}

class TicketPool1 {
    String roomName;
    List<Ticket1> tickets = new ArrayList<>();
    int price = 250000;

    public TicketPool1(String roomName, int capacity) {
        this.roomName = roomName;

        for (int i = 1; i <= capacity; i++) {
            tickets.add(new Ticket1(roomName + "-" + String.format("%03d", i)));
        }
    }

    public synchronized Ticket1 sellTicket() {
        for (Ticket1 t : tickets) {
            if (!t.sold) {
                t.sold = true;
                return t;
            }
        }
        return null;
    }

    public int soldCount() {
        int count = 0;
        for (Ticket1 t : tickets) {
            if (t.sold) count++;
        }
        return count;
    }

    public int total() {
        return tickets.size();
    }

    public int revenue() {
        return soldCount() * price;
    }
}

class BookingCounter1 implements Runnable {
    String name;
    TicketPool1 pool;
    boolean running = true;

    public BookingCounter1(String name, TicketPool1 pool) {
        this.name = name;
        this.pool = pool;
    }

    public void stopCounter() {
        running = false;
    }

    @Override
    public void run() {
        System.out.println(name + " bắt đầu bán vé...");
        while (running) {
            Ticket1 t = pool.sellTicket();

            if (t != null) {
                System.out.println(name + " bán vé " + t.id);
            }

            try {
                Thread.sleep(1000);
            } catch (Exception e) {}
        }
    }
}

class DeadlockDetector{
    public static void detect() {
        System.out.println("Đang quét deadlock...");

        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] ids = bean.findDeadlockedThreads();

        if (ids != null) {
            System.out.println("Phát hiện deadlock");
        } else {
            System.out.println("Không phát hiện deadlock.");
        }
    }
}

class CinemaSimulation {
    TicketPool1 roomA;
    TicketPool1 roomB;

    ExecutorService executor;
    List<BookingCounter1> counters = new ArrayList<>();

    boolean running = false;

    public void start(int rooms, int tickets, int counterCount) {
        roomA = new TicketPool1("A", tickets);
        roomB = new TicketPool1("B", tickets);

        executor = Executors.newFixedThreadPool(counterCount);

        System.out.println("Đã khởi tạo hệ thống với " + rooms + " phòng, " + (rooms * tickets) + " vé, " + counterCount + " quầy");

        for (int i = 1; i <= counterCount; i++) {
            TicketPool1 pool = (i % 2 == 0) ? roomB : roomA;

            BookingCounter1 counter = new BookingCounter1("Quầy " + i, pool);

            counters.add(counter);
            executor.submit(counter);
        }

        running = true;
    }

    public void pause() {
        for (BookingCounter1 c : counters) {
            c.stopCounter();
        }

        System.out.println("Đã tạm dừng tất cả quầy bán vé.");
    }

    public void resume(int counterCount) {
        executor = Executors.newFixedThreadPool(counterCount);

        for (int i = 0; i < counters.size(); i++) {

            BookingCounter1 c = counters.get(i);

            BookingCounter1 newCounter = new BookingCounter1(c.name, c.pool);

            counters.set(i, newCounter);

            executor.submit(newCounter);
        }

        System.out.println("Đã tiếp tục hoạt động.");
    }

    public void stats() {
        System.out.println("==== THỐNG KÊ HIỆN TẠI ====");

        System.out.println("Phòng A: Đã bán " + roomA.soldCount() + "/" + roomA.total());

        System.out.println("Phòng B: Đã bán " + roomB.soldCount() + "/" + roomB.total());

        int revenue = roomA.revenue() + roomB.revenue();

        System.out.println("Tổng doanh thu: " + revenue + " VND");
    }

    public void shutdown() {

        executor.shutdownNow();

        System.out.println("Đang dừng hệ thống...");
    }
}

public class Bai6 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CinemaSimulation sim = new CinemaSimulation();

        while (true) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Bắt đầu mô phỏng");
            System.out.println("2. Tạm dừng mô phỏng");
            System.out.println("3. Tiếp tục mô phỏng");
            System.out.println("4. Thêm vé vào phòng");
            System.out.println("5. Xem thống kê");
            System.out.println("6. Phát hiện deadlock");
            System.out.println("7. Thoát");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Nhập số phòng: ");
                    int rooms = sc.nextInt();

                    System.out.print("Số vé/phòng: ");
                    int tickets = sc.nextInt();

                    System.out.print("Số quầy: ");
                    int counters = sc.nextInt();

                    sim.start(rooms, tickets, counters);
                    break;

                case 2:
                    sim.pause();
                    break;
                case 3:
                    sim.resume(3);
                    break;
                case 5:
                    sim.stats();
                    break;

                case 6:
                    DeadlockDetector.detect();
                    break;
                case 7:
                    sim.shutdown();

                    System.out.println("Kết thúc chương trình");
                    return;
            }
        }
    }
}