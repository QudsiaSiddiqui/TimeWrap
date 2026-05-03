function StateViewer({ state }) {
  return (
    <div>
      <h2>State</h2>
      <pre>{JSON.stringify(state, null, 2)}</pre>
    </div>
  );
}

export default StateViewer;