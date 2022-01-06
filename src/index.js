import React from 'react';
import ReactDOM from 'react-dom';
import IndexPage from './pages/IndexPage';
import ForumIndex from './pages/forums/ForumIndex';
import ForumPage from './pages/forums/ForumPage';
import ThreadPage from './pages/forums/ThreadPage';
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
            <Route path="/forums" element={<ForumIndex/>} />
            <Route path="/forums/:forumId" element={<ForumPage/>} />
            <Route path="/forums/thread/:threadId" element={<ThreadPage/>} />
        </Routes>
        <Footer />
      </div>
    </Router>
  </React.StrictMode>,
  document.getElementById('root')
);
