import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import SensorList from './pages/SensorList';
import SensorDetails from './pages/SensorDetail';
import UserProfile from './pages/UserProfile';
import LocationList from './pages/LocationList';
import ReportsList from './pages/ReportList';
import AlertsList from './pages/AlertList';
import AdminDashboard from './pages/AdminDashBoard';
import AdminDatabase from './pages/AdminDataBase';

import Header from './components/Header';
import "./i18.js";

export default function App() {
  return (
    <Router>
      <Header />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/sensors" element={<SensorList />} />
        <Route path="/sensor-detail/:sensorId" element={<SensorDetails />} />
        <Route path="/profile" element={<UserProfile />} />
        <Route path="/locations" element={<LocationList />} />
        <Route path="/reports" element={<ReportsList />} />
        <Route path="/alerts" element={<AlertsList />} />
        <Route path="/admin" element={<AdminDashboard />} />
        <Route path="/admin/database" element={<AdminDatabase />} />
      </Routes>
    </Router>
  );
}
