import React from 'react';
import ReactDOM from 'react-dom';
import IndexPage from './pages/IndexPage';
import ForumPage from './pages/forums/ForumIndex';
import Header from './components/default/Header';
import Footer from './components/default/Footer';
import './styles/Default.css'
import './styles/utils/Helpers.css'
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';

ReactDOM.render(
  <React.StrictMode>
    <Router>
      <div>
        <Header />
        <Routes>
            <Route path="/" element={<IndexPage />} />
            <Route path="/forums" element={<ForumPage/>} />
            <Route path="/forums/:forumId" element={<ForumPage/>} />
        </Routes>
        <Footer />
      </div>
    </Router>
  </React.StrictMode>,
  document.getElementById('root')
);
