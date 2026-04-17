import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api';

function SkillRequest() {
  const navigate = useNavigate();
  const [skillName, setSkillName] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);

  const handleRequest = async () => {
    try {
      const res = await API.post('/requests/create', {
        skillName
      });
      setResult(res.data);
      setMessage('Request posted successfully!');
    } catch (err) {
      setError('Failed to post request. Try again.');
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
        <h2>Post Skill Request</h2>

        <label>Skill Name</label>
        <input
          placeholder="Enter skill you want to learn"
          value={skillName}
          onChange={(e) => setSkillName(e.target.value)}
        />

        <button onClick={handleRequest}>
          Find Teacher
        </button>

        {message &&
          <div className="message">{message}</div>}
        {error &&
          <div className="error">{error}</div>}

        {result && (
          <div className="card"
            style={{ marginTop: '12px' }}>
            <p>Status: <strong>{result.status}</strong></p>
            {result.teacherName && (
              <p>Teacher: <strong>
                {result.teacherName}
              </strong></p>
            )}
            {result.requestId && (
              <p>Request ID: <strong>
                {result.requestId}
              </strong></p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default SkillRequest;