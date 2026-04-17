import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import SkillRequest from './pages/SkillRequest';
import Session from './pages/Session';
import Feedback from './pages/Feedback';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/"          element={<Login />} />
        <Route path="/register"  element={<Register />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/request"   element={<SkillRequest />} />
        <Route path="/session"   element={<Session />} />
        <Route path="/feedback"  element={<Feedback />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;