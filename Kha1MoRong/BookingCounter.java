package Kha1MoRong;

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

            if (ticket != null) {

                soldCount++;
                System.out.println(counterName + " đã bán vé " + ticket.getTicketId());

            } else {

                if (TicketSupplier.finished &&
                        roomA.getRemainingTickets() == 0 &&
                        roomB.getRemainingTickets() == 0) {
                    break;
                }

                try {
                    Thread.sleep(200);
                } catch (Exception e) {}
            }
        }
    }
}