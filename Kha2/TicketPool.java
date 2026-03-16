package Kha2;

import java.util.ArrayList;
import java.util.List;

public class TicketPool {
    private String roomName;
    private List<Ticket> tickets = new ArrayList<>();
    private int ticketCounter = 0;

    public TicketPool(String roomName, int total) {

        this.roomName = roomName;

        for (int i = 1; i <= total; i++) {
            ticketCounter++;
            String id = roomName + "-" + String.format("%03d", ticketCounter);
            tickets.add(new Ticket(id, roomName));
        }
    }

    public synchronized Ticket sellTicket() {
        while (true) {
            for (Ticket t : tickets) {
                if (!t.isSold()) {
                    t.setSold(true);
                    return t;
                }
            }

            try {
                System.out.println("Hết vé phòng " + roomName + ", đang chờ...");
                wait(); // chờ vé mới
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void addTickets(int count) {
        for (int i = 0; i < count; i++) {
            ticketCounter++;
            String id = roomName + "-" + String.format("%03d", ticketCounter);
            tickets.add(new Ticket(id, roomName));
        }

        System.out.println("Nhà cung cấp: Đã thêm " + count + " vé vào phòng " + roomName);

        notifyAll();
    }
}