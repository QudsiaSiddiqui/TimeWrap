import ReactFlow, { Background, Controls } from "reactflow";
import "reactflow/dist/style.css";

function TimelineGraph({ data, onSelect ,onCheckout,onSource,onTarget,head,selectedEvent}) {

  // Convert events → nodes
  const nodes = data.map((event, index) => {
    let bg = "#eee";
    if (event.id === head) {
    bg = "#4caf50"; // 🟢 HEAD
  } else if (event.id === selectedEvent) {
    bg = "#2196f3"; // 🔵 selected
  }

  return {
  id: event.id.toString(),
  data: {
    label: (
      <div>
        <div>Event {event.id}</div>

        <button onClick={() => onSelect(event.id)}>View</button>
        <button onClick={() => onCheckout(event.id)}>Checkout</button>
        <button onClick={() => onSource(event.id)}>Source</button>
        <button onClick={() => onTarget(event.id)}>Target</button>
      </div>
    )
  },
  position: { x: index * 200, y: 100 },style: {
      background: bg,
      color: "#000",
      padding: 10,
      border: "1px solid black"
    }
};
});

  // Convert parent → edges
  const edges = data
    .filter(e => e.parentId)
    .map(e => ({
      id: `e${e.parentId}-${e.id}`,
      source: e.parentId.toString(),
      target: e.id.toString()
    }));

  return (
    <div style={{ height: "500px", width: "600px" }}>
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodeClick={(e, node) => onSelect(node.id)}
      >
        <Background />
        <Controls />
      </ReactFlow>
    </div>
  );
}

export default TimelineGraph;