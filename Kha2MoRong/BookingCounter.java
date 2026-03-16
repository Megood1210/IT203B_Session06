package Kha2MoRong;

public class BookingCounter implements Runnable {
    private String name;
    private TicketPool roomA;
    private TicketPool roomB;
    private boolean lockAFirst;

    public BookingCounter(String name, TicketPool roomA, TicketPool roomB, boolean lockAFirst) {
        this.name = name;
        this.roomA = roomA;
        this.roomB = roomB;
        this.lockAFirst = lockAFirst;
    }
    private void sellCombo() {
        synchronized (roomA) {
            synchronized (roomB) {
                Ticket a = roomA.getAvailableTicket();
                Ticket b = roomB.getAvailableTicket();

                if (a == null || b == null) {
                    System.out.println(name + ": Hết vé phòng " + (a == null ? "A" : "B") + ", bán combo thất bại");
                    return;
                }

                a.setSold(true);
                b.setSold(true);

                System.out.println(name + " bán combo thành công: "
                        + a.getTicketId() + " & " + b.getTicketId());
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            if (roomA.getAvailableTicket() == null || roomB.getAvailableTicket() == null) {
                System.out.println(name + ": Hết vé phòng A hoặc B,bán combo thất bại");
                break;
            }

            sellCombo();

            try {
                Thread.sleep(500);
            } catch (Exception e) {}
        }
    }
}