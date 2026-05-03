package com.timewrap.timewrap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.timewrap.timewrap.entity.Event;
import com.timewrap.timewrap.service.EventService;
import com.timewrap.timewrap.service.TimeControlService;

@RestController
@RequestMapping("/time")
public class TimeController {

     @Autowired
    private TimeControlService timeControlService;

    @Autowired
    private EventService eventService;

     @PostMapping("/undo")
    public String undo() {
        Event reverse = timeControlService.undo();
        eventService.saveSystemEvent(reverse);
        //eventService.saveEvent(reverse,true);
        return "Undo applied";
    }

     @PostMapping("/redo")
public String redo() {
    Event event = timeControlService.redo();

    System.out.println("REDO EVENT DATA: " + event.getEventData());
    eventService.saveSystemEvent(event);
   // eventService.saveEvent(event,true);
    return "Redo applied";
}
    @GetMapping("/test")
    public String test() {
        return "TimeController working";
    }
}
