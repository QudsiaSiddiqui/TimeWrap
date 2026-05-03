package com.timewrap.timewrap.service;

import java.time.LocalDateTime;
import java.util.Stack;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timewrap.timewrap.entity.Event;

@Service
public class TimeControlService {

    private Stack<Event> undoStack =new Stack<>();
    private Stack<Event> redoStack = new Stack<>();
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void recordEvent(Event event, boolean isSystemEvent){
        undoStack.push(event);
        if(!isSystemEvent){
        redoStack.clear();
        }
    }

    public Event undo(){
        if(undoStack.isEmpty()){
            throw new RuntimeException("Nothing to undo");
        }
        Event last=undoStack.pop();
        redoStack.push(last);

        return reverseEvent(last);
    }
 public Event redo() {
    if (redoStack.isEmpty()) {
        throw new RuntimeException("Nothing to redo");
    }

    Event original = redoStack.pop();

    System.out.println("----- REDO DEBUG -----");
    System.out.println("Event Type: " + original.getEventType());
    System.out.println("Event Data: " + original.getEventData());
    System.out.println("----------------------");

     Event redoEvent = new Event();
    redoEvent.setEntityId(original.getEntityId());
    redoEvent.setEventType(original.getEventType());

    // ✅ ALWAYS use AFTER STATE as new input
    redoEvent.setEventData(original.getAfterState());

    redoEvent.setTimestamp(LocalDateTime.now());

    undoStack.push(original);

    return redoEvent;
}

    private Event reverseEvent(Event event) {

         try {
    Event reverse = new Event();

    reverse.setEntityId(event.getEntityId());
    reverse.setTimestamp(LocalDateTime.now());

    switch (event.getEventType()) {

        case "CREATE":
            reverse.setEventType("DELETE");
            break;

        case "DELETE":
            reverse.setEventType("CREATE");
            reverse.setEventData(event.getEventData());
            break;

        case "UPDATE":
            // For now simple rollback placeholder
            reverse.setEventType("UPDATE");
            reverse.setEventData(event.getBeforeState());
            break;
    }
    return reverse;
    } catch (Exception e) {
            throw new RuntimeException("Undo failed", e);
        }

}
}
