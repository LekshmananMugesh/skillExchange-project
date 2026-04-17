import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api';

function Register() {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleRegister = async () => {
    try {
      const res = await API.post('/auth/register', {
        name, email, password
      });
      setMessage(res.data);
      setTimeout(() => navigate('/'), 1500);
    } catch (err) {
      setError('Registration failed. Try again.');
    }
  };

  return (
    <div className="container">
      <h2>SkillExchange — Register</h2>

      <label>Name</label>
      <input
        type="text"
        placeholder="Enter your name"
        value={name}
        onChange={(e) => setName(e.target.value)}
      />

      <label>Email</label>
      <input
        type="email"
        placeholder="Enter your email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />

      <label>Password</label>
      <input
        type="password"
        placeholder="Enter your password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <button onClick={handleRegister}>Register</button>

      {message && <div className="message">{message}</div>}
      {error   && <div className="error">{error}</div>}

      <span
        className="link"
        onClick={() => navigate('/')}>
        Already have an account? Login
      </span>
    </div>
  );
}

export default Register;