import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api';

function Feedback() {
  const navigate = useNavigate();
  const [sessionId, setSessionId] = useState('');
  const [rating, setRating] = useState('');
  const [comment, setComment] = useState('');
  const [teacherId, setTeacherId] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [teacherRating, setTeacherRating] = useState(null);

  const submitFeedback = async () => {
    try {
      const res = await API.post('/feedback/submit', {
        sessionId, rating, comment
      });
      setMessage(res.data);
    } catch (err) {
      setError('Failed to submit feedback');
    }
  };

  const getTeacherRating = async () => {
    try {
      const res = await API.get(
        `/feedback/teacher/${teacherId}`);
      setTeacherRating(res.data);
    } catch (err) {
      setError('Failed to get teacher rating');
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
        <h2>Feedback</h2>

        {message &&
          <div className="message">{message}</div>}
        {error &&
          <div className="error">{error}</div>}

        <label>Session ID</label>
        <input
          placeholder="Enter session ID"
          value={sessionId}
          onChange={(e) => setSessionId(e.target.value)}
        />
        <label>Rating (1 to 5)</label>
        <input
          placeholder="Enter rating"
          value={rating}
          onChange={(e) => setRating(e.target.value)}
        />
        <label>Comment</label>
        <input
          placeholder="Enter your comment"
          value={comment}
          onChange={(e) => setComment(e.target.value)}
        />
        <button onClick={submitFeedback}>
          Submit Feedback
        </button>

        <hr style={{ margin: '20px 0' }} />

        <h3 style={{ marginBottom: '8px' }}>
          Check Teacher Rating
        </h3>
        <label>Teacher ID</label>
        <input
          placeholder="Enter teacher ID"
          value={teacherId}
          onChange={(e) => setTeacherId(e.target.value)}
        />
        <button onClick={getTeacherRating}>
          Get Rating
        </button>

        {teacherRating && (
          <div className="card"
            style={{ marginTop: '12px' }}>
            <p>Teacher: <strong>
              {teacherRating.teacherName}
            </strong></p>
            <p>Average Rating: <strong>
              {teacherRating.averageRating}
            </strong></p>
            <p>Total Sessions: <strong>
              {teacherRating.totalSessions}
            </strong></p>
          </div>
        )}
      </div>
    </div>
  );
}

export default Feedback;