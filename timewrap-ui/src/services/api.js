import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080"
});

export const getTimeline = (entityId) =>
  API.get(`/events/${entityId}/timeline`);

export const getStateByEvent = (entityId, eventId) =>
  API.get(`/events/${entityId}/state`, {
    params: { eventId }
  });

export const checkout = (entityId, eventId) =>
  API.post(`/events/${entityId}/checkout`, null, {
    params: { eventId }
  });

export const merge = (entityId, sourceEventId, targetEventId, strategy) =>
  API.post(`/events/${entityId}/merge`, null, {
    params: { sourceEventId, targetEventId, strategy }
  });

export const getHead = (entityId) =>
  API.get(`/events/${entityId}/head`);
export const getDiff = (entityId, from, to) =>
  API.get(`/events/${entityId}/diff`, {
    params: { fromEventId: from, toEventId: to }
  });