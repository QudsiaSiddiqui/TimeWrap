import { useEffect, useState } from "react";
import TimelineGraph from "./components/TimelineGraph";
import StateViewer from "./components/StateViewer";
import DiffViewer from "./components/DiffViewer";

import {
  getTimeline,
  getStateByEvent,
  checkout,
  merge,
  getDiff,
  getHead
} from "./services/api";

function App() {
  const entityId = "user1";

  const [timeline, setTimeline] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [state, setState] = useState(null);

  const [sourceEvent, setSourceEvent] = useState(null);
  const [targetEvent, setTargetEvent] = useState(null);

  const [mergeResult, setMergeResult] = useState(null);
  const [head, setHead] = useState(null);
  const [diff, setDiff] = useState(null);

  useEffect(() => {
    loadTimeline();
  }, []);

  const loadTimeline = async () => {
    const timelineRes = await getTimeline(entityId);
    setTimeline(timelineRes.data);

    const headRes = await getHead(entityId);
    setHead(headRes.data);
  };

  const handleSelect = async (eventId) => {
    setSelectedEvent(eventId);

    const res = await getStateByEvent(entityId, eventId);
    setState(res.data);
  };

  const handleCheckout = async (eventId) => {
    await checkout(entityId, eventId);
    setHead(eventId);
  };

  const handleSelectSource = (eventId) => {
    setSourceEvent(eventId);
    setDiff(null);
  };

  const handleSelectTarget = (eventId) => {
    setTargetEvent(eventId);
    setDiff(null);
  };

  const handleMerge = async (strategy) => {
    if (!sourceEvent || !targetEvent) {
      alert("Select source and target");
      return;
    }

    const res = await merge(
      entityId,
      sourceEvent,
      targetEvent,
      strategy
    );

    setMergeResult(res.data);
  };

  const handleDiff = async () => {
    if (!sourceEvent || !targetEvent) {
      alert("Select source and target first");
      return;
    }

    const res = await getDiff(
      entityId,
      sourceEvent,
      targetEvent
    );

    setDiff(res.data);
  };

  return (
    <div style={{ display: "flex", gap: "20px", padding: "20px" }}>
      
      <div>
        <h3>Current HEAD: {head || "None"}</h3>

        <TimelineGraph
          data={timeline}
          onSelect={handleSelect}
          onCheckout={handleCheckout}
          onSource={handleSelectSource}
          onTarget={handleSelectTarget}
          head={head}
          selectedEvent={selectedEvent}
        />
      </div>

      <div style={{ minWidth: "500px" }}>
        <StateViewer state={state} />

        <h3>Merge</h3>

        <div>
          <strong>Source:</strong> {sourceEvent || "None"}
        </div>

        <div>
          <strong>Target:</strong> {targetEvent || "None"}
        </div>

        <button
          disabled={!sourceEvent || !targetEvent}
          onClick={handleDiff}
        >
          Show Diff
        </button>

        <button onClick={() => handleMerge("SOURCE_WINS")}>
          Merge (Source Wins)
        </button>

        <button onClick={() => handleMerge("TARGET_WINS")}>
          Merge (Target Wins)
        </button>

        <button onClick={() => handleMerge("MANUAL")}>
          Merge (Manual)
        </button>

        <DiffViewer diff={diff} />

        {mergeResult && (
          <div>
            <h3>Merged State</h3>
            <pre>
              {JSON.stringify(
                mergeResult.mergedState,
                null,
                2
              )}
            </pre>

            <h3>Conflicts</h3>
            <pre>
              {JSON.stringify(
                mergeResult.conflicts,
                null,
                2
              )}
            </pre>
          </div>
        )}
      </div>

    </div>
  );
}

export default App;