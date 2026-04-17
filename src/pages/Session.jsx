import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api';

function Session() {
  const navigate = useNavigate();
  const [sessionId, setSessionId] = useState('');
  const [requestId, setRequestId] = useState('');
  const [meetLink, setMeetLink] = useState('');
  const [scheduledAt, setScheduledAt] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const createSession = async () => {
    try {
      const res = await API.post('/sessions/create', {
        requestId, meetLink, scheduledAt
      });
      setMessage('Session created! ID: '
        + res.data.sessionId);
    } catch (err) {
      setError('Failed to create session');
    }
  };

  const joinSession = async () => {
    try {
      const res = await API.post(
        `/sessions/join/${sessionId}`);
      setMessage(res.data);
    } catch (err) {
      setError('Failed to join session');
    }
  };

  const confirmSession = async () => {
    try {
      const res = await API.post(
        `/sessions/confirm/${sessionId}`);
      setMessage(res.data);
    } catch (err) {
      setError('Failed to confirm session');
    }
  };

  return (
    <div>
      <div className="navbar">
        <h1>SkillExchange</h1>
        <button onClick={() => navigate('/dashboard')}>
          Back
        </button>
      </div>

      <div className="container">
        <h2>Session</h2>

        {message &&
          <div className="message">{message}</div>}
        {error &&
          <div className="error">{error}</div>}

        <h3 style={{ margin: '16px 0 8px' }}>
          Create Session
        </h3>
        <label>Request ID</label>
        <input
          placeholder="Enter request ID"
          value={requestId}
          onChange={(e) => setRequestId(e.target.value)}
        />
        <label>Meet Link</label>
        <input
          placeholder="https://meet.google.com/..."
          value={meetLink}
          onChange={(e) => setMeetLink(e.target.value)}
        />
        <label>Scheduled At</label>
        <input
          placeholder="2026-04-08T10:00:00"
          value={scheduledAt}
          onChange={(e) => setScheduledAt(e.target.value)}
        />
        <button onClick={createSession}>
          Create Session
        </button>

        <hr style={{ margin: '20px 0' }} />

        <h3 style={{ marginBottom: '8px' }}>
          Join or Confirm Session
        </h3>
        <label>Session ID</label>
        <input
          placeholder="Enter session ID"
          value={sessionId}
          onChange={(e) => setSessionId(e.target.value)}
        />
        <button onClick={joinSession}>
          Join Session
        </button>
        <button
          style={{ backgroundColor: '#2e7d32' }}
          onClick={confirmSession}>
          Confirm Session
        </button>

        <button
          style={{ marginTop: '12px',
            backgroundColor: '#1565c0' }}
          onClick={() => navigate('/feedback')}>
          Go to Feedback
        </button>
      </div>
    </div>
  );
}

export default Session;