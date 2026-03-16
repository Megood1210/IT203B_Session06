package Kha2;

public class BookingCounter implements Runnable {
    private String counterName;
    private TicketPool roomA;
    private TicketPool roomB;
    private int soldCount = 0;

    public BookingCounter(String counterName, TicketPool roomA, TicketPool roomB) {
        this.counterName = counterName;
        this.roomA = roomA;
        this.roomB = roomB;
    }

    public int getSoldCount() {
        return soldCount;
    }

    @Override
    public void run() {
        while (true) {
            Ticket ticket = roomA.sellTicket();

            if (ticket == null) {
                ticket = roomB.sellTicket();
            }

            soldCount++;

            System.out.println(counterName + " đã bán vé " + ticket.getTicketId());

            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
        }
    }
}