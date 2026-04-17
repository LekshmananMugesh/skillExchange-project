import axios from 'axios';

// base URL for all API calls
const API = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// automatically add token to every request
API.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default API;