package Kha1MoRong;

public class Main {
    public static void main(String[] args) {
        TicketPool roomA = new TicketPool("A", 10);
        TicketPool roomB = new TicketPool("B", 10);

        BookingCounter counter1 = new BookingCounter("Quầy 1", roomA, roomB);
        BookingCounter counter2 = new BookingCounter("Quầy 2", roomA, roomB);

        TicketSupplier supplier = new TicketSupplier(roomA, roomB, 3, 3000, 1);

        Thread t1 = new Thread(counter1);
        Thread t2 = new Thread(counter2);
        Thread t3 = new Thread(supplier);

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Quầy 1 bán được: " + counter1.getSoldCount() + " vé");
        System.out.println("Quầy 2 bán được: " + counter2.getSoldCount() + " vé");

        System.out.println("Vé còn lại phòng A: " + roomA.getRemainingTickets());
        System.out.println("Vé còn lại phòng B: " + roomB.getRemainingTickets());
    }
}
