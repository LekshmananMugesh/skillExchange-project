import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api';

function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleLogin = async () => {
    try {
      const res = await API.post('/auth/login', {
        email, password
      });
      // save token in localStorage
      localStorage.setItem('token', res.data.token);
      navigate('/dashboard');
    } catch (err) {
      setError('Wrong email or password');
    }
  };

  return (
    <div className="container">
      <h2>SkillExchange — Login</h2>

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

      <button onClick={handleLogin}>Login</button>

      {message && <div className="message">{message}</div>}
      {error   && <div className="error">{error}</div>}

      <span
        className="link"
        onClick={() => navigate('/register')}>
        Don't have an account? Register
      </span>
    </div>
  );
}

export default Login;