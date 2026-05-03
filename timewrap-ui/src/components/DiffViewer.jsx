function DiffViewer({ diff }) {
  if (!diff) return null;

  return (
    <div style={{ marginTop: "20px" }}>
      <h3>Diff Viewer</h3>

      {Object.keys(diff).map((key) => {
        const change = diff[key];

        return (
          <div
            key={key}
            style={{
              display: "flex",
              border: "1px solid #ccc",
              marginBottom: "10px"
            }}
          >
            {/* BEFORE */}
            <div
              style={{
                flex: 1,
                padding: "10px",
                background: "#ffecec"
              }}
            >
              <strong>{key}</strong>
              <div>Before:</div>
              <pre>{JSON.stringify(change.before, null, 2)}</pre>
            </div>

            {/* AFTER */}
            <div
              style={{
                flex: 1,
                padding: "10px",
                background: "#eaffea"
              }}
            >
              <strong>{key}</strong>
              <div>After:</div>
              <pre>{JSON.stringify(change.after, null, 2)}</pre>
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default DiffViewer;