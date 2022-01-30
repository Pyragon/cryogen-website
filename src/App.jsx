import React, { useState, useMemo, useEffect } from 'react'
import axios from './utils/axios';
import Noty from 'noty';
import IndexPage from './pages/IndexPage';
import ForumIndex from './pages/forums/ForumIndex';
import ForumPage from './pages/forums/ForumPage';
import ThreadPage from './pages/forums/ThreadPage';
import Header from './components/default/Header';
import Footer from './components/default/Footer';
import './styles/Default.css';
import './styles/utils/Helpers.css';
import '../node_modules/noty/lib/noty.css';
import './styles/noty/cryogen.css';
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';

import UserContext from './utils/contexts/UserContext';
import NewThread from './components/forums/threads/NewThread';
import RegisterPage from './pages/account/RegisterPage';
import PrivatePage from './pages/forums/private/PrivatePage';

export default function App() {
    let [ user, setUser ] = useState(null);

    useEffect(() => {
        axios.get('/users/auth')
            .then(res => res.data.result && setUser(res.data.user));
    }, []);

    let providerValue = useMemo(() => ({ user, setUser }), [ user, setUser ]);
    return (
        <React.StrictMode>
          <Router>
            <div>
              <UserContext.Provider value={providerValue}>
                <Header />
                <Routes>
                    <Route path="/" element={<IndexPage />} />
                    <Route path="/register" element={<RegisterPage/>}/>
                    <Route path="/forums" element={<ForumIndex/>} />
                    <Route path="/forums/private" element={<PrivatePage/> } />
                    <Route path="/forums/private/:section" element={<PrivatePage/> } />
                    <Route path="/forums/:forumId" element={<ForumPage/>} />
                    <Route path="/forums/:forumId/new" element={<NewThread/>} />
                    <Route path="/forums/threads/:threadId" element={<ThreadPage/>} />
                    <Route path="/forums/threads/:threadId/:page" element={<ThreadPage/>} />
                </Routes>
                <Footer />
              </UserContext.Provider>
            </div>
          </Router>
        </React.StrictMode>
    )
}
