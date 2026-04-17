import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api';

function Dashboard() {
  const navigate = useNavigate();
  const [credits, setCredits] = useState(0);
  const [teachSkills, setTeachSkills] = useState([]);
  const [learnSkills, setLearnSkills] = useState([]);
  const [teachInput, setTeachInput] = useState('');
  const [learnInput, setLearnInput] = useState('');
  const [message, setMessage] = useState('');

  // load data when page opens
  useEffect(() => {
    loadCredits();
    loadSkills();
  }, []);

  const loadCredits = async () => {
    const res = await API.get('/auth/credits');
    setCredits(res.data.credits);
  };

  const loadSkills = async () => {
    const res = await API.get('/skills/my-skills');
    setTeachSkills(res.data.teach);
    setLearnSkills(res.data.learn);
  };

  const addTeachSkill = async () => {
    await API.post('/skills/teach',
      { skillName: teachInput });
    setMessage('Teach skill added!');
    setTeachInput('');
    loadSkills();
  };

  const addLearnSkill = async () => {
    await API.post('/skills/learn',
      { skillName: learnInput });
    setMessage('Learn skill added!');
    setLearnInput('');
    loadSkills();
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/');
  };

  return (
    <div>
      <div className="navbar">
        <h1>SkillExchange</h1>
        <button onClick={handleLogout}>Logout</button>
      </div>

      <div className="container">
        <h2>Dashboard</h2>

        <div className="card">
          Credits: <strong>{credits}</strong>
        </div>

        {message &&
          <div className="message">{message}</div>}

        <h3 style={{ margin: '16px 0 8px' }}>
          My Teach Skills
        </h3>
        {teachSkills.map((s, i) => (
          <div className="card" key={i}>{s}</div>
        ))}
        <input
          placeholder="Add skill you can teach"
          value={teachInput}
          onChange={(e) => setTeachInput(e.target.value)}
        />
        <button onClick={addTeachSkill}>
          Add Teach Skill
        </button>

        <h3 style={{ margin: '16px 0 8px' }}>
          My Learn Skills
        </h3>
        {learnSkills.map((s, i) => (
          <div className="card" key={i}>{s}</div>
        ))}
        <input
          placeholder="Add skill you want to learn"
          value={learnInput}
          onChange={(e) => setLearnInput(e.target.value)}
        />
        <button onClick={addLearnSkill}>
          Add Learn Skill
        </button>

        <button
          style={{ marginTop: '20px',
            backgroundColor: '#1565c0' }}
          onClick={() => navigate('/request')}>
          Post Skill Request
        </button>

        <button
          style={{ backgroundColor: '#2e7d32' }}
          onClick={() => navigate('/session')}>
          My Sessions
        </button>
      </div>
    </div>
  );
}

export default Dashboard;