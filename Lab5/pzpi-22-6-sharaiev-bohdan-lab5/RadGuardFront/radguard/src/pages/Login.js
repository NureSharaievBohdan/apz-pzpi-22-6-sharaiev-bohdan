import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../styles/Login.css';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    const res = await fetch('http://localhost:8000/api/users/auth/login/', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    if (res.ok) {
      const tokens = await res.json();
      localStorage.setItem('accessToken', tokens.access);  
      alert('Login successful!');
      localStorage.setItem('accessToken', tokens.access);
      localStorage.setItem('refreshToken', tokens.refresh);

      const userRes = await fetch('http://localhost:8000/api/users/profile/', {
        headers: {
          'Authorization': `Bearer ${tokens.access}`
        }
      });
   
      const userData = await userRes.json();

      if (userData.role === 'admin') {
        alert('Login successful! Role: admin');
        navigate('/admin'); 
      } else {
        alert(`Login successful! Role: ${userData.role}`);
        navigate('/devices'); 
      }

    } else {
      alert('Login failed');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="form-container">
      <h2>Login</h2>
      <input
        type="email" placeholder="email"
        value={email} onChange={e => setEmail(e.target.value)}
        required
      />
      <input
        type="password" placeholder="Password"
        value={password} onChange={e => setPassword(e.target.value)}
        required
      />
      <button type="submit">Login</button>
      <p>
        No account? <Link to="/register">Register here</Link>
      </p>
    </form>
  );
}
