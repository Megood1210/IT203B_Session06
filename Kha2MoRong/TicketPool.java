package Kha2MoRong;

import java.util.ArrayList;
import java.util.List;

public class TicketPool {
    private String roomName;
    private List<Ticket> tickets = new ArrayList<>();

    public TicketPool(String roomName, int total) {

        this.roomName = roomName;

        for (int i = 1; i <= total; i++) {
            String id = roomName + "-" + String.format("%03d", i);
            tickets.add(new Ticket(id));
        }
    }

    public Ticket getAvailableTicket() {
        for (Ticket t : tickets) {
            if (!t.isSold()) {
                return t;
            }
        }
        return null;
    }

    public String getRoomName() {
        return roomName;
    }
}