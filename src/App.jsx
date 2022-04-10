import React, { useState, useMemo, useEffect, useRef } from 'react'
import axios from './utils/axios';
import { v4 as uuidv4 } from 'uuid';
import IndexPage from './pages/IndexPage';
import ForumIndex from './pages/forums/ForumIndex';
import ForumPage from './pages/forums/ForumPage';
import ThreadPage from './pages/forums/ThreadPage';
import Header from './components/default/Header';
import Footer from './components/default/Footer';
import './styles/Default.css';
import './styles/utils/Helpers.css';
import './styles/utils/Notifications.css';
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';

import UserContext from './utils/contexts/UserContext';
import NotificationContext from './utils/contexts/NotificationContext';
import NewThread from './components/forums/threads/NewThread';
import RegisterPage from './pages/account/RegisterPage';
import PrivatePage from './pages/forums/private/PrivatePage';
import Notification from './components/utils/notifications/Notification';
import Button from './components/utils/Button';
import { useLayoutEffect } from 'react';

export default function App() {
    let [user, setUser] = useState(null);
    let modal = useRef();
    let [ modalButtons, setModalButtons ] = useState([]);
    let [ modalContent, setModalContent ] = useState(<></>);
    let [ modalOpen, setModalOpen ] = useState(false);

    let [ notifications, setNotifications ] = useState([]);

    function sendNotification({ text, timeout=5000, template, onClick }) {
        let id = uuidv4();
        let remove = () => setNotifications(notifications => notifications.filter(n => n.id !== id));
        if(!onClick)
            onClick = remove;
        if(timeout) setTimeout(remove, timeout);
        setNotifications(notifications => [ { id, text, timeout, template, onClick }, ...notifications ]);
    }

    function sendConfirmation({ text, onSuccess, onClose }) {
        let buttons = [
            {
                title: 'Yes',
                column: 3,
                className: 'btn-success',
                onClick: () => {
                    let close = () => setModalOpen(false);
                    if(onSuccess) onSuccess(close);
                    else close();
                }   
            },
            {
                title: 'Cancel',
                column: 4,
                className: 'btn-danger',
                onClick: () => {
                    setModalOpen(false);
                    if(onClose) onClose();
                }
            }
        ];
        let contents = <p style={{textAlign: 'center'}}>{text}</p>;
        openModal({ contents, buttons });
    }

    function openModal({ contents, buttons }) {
        setModalOpen(true);
        setModalContent(contents);
        setModalButtons(buttons);
    }

    function closeModal() {
        setModalOpen(false);
    }

    function sendErrorNotification(error) {
        console.error(error);
        sendNotification({ text: <span className='red'>Error</span>, timeout: 10000 });
    }

    function repositionModal() {
        if(!modal.current) return;
        modal.current.style.top = (window.innerHeight - modal.current.offsetHeight) / 2 + 'px';
        modal.current.style.left = (window.innerWidth - modal.current.offsetWidth) / 2 + 'px';
    }

    useEffect(() => {
        axios.get('/users/auth')
            .then(res => res.data.result && setUser(res.data.user));
        window.addEventListener('resize', repositionModal);
    }, []);

    useLayoutEffect(() => {
        repositionModal();
    }, [modalOpen]);

    let providerValue = useMemo(() => ({ user, setUser }), [user, setUser]);
    return (
        <React.StrictMode>
            <Router>
                <div>
                    <UserContext.Provider value={providerValue}>
                        <NotificationContext.Provider value={{ notifications, setNotifications, sendConfirmation, openModal, closeModal, sendNotification, sendErrorNotification }}>
                            <Header />
                            { modalOpen && 
                                <div ref={modal} className='modal'>
                                    <div className='modal-content'>
                                        {modalContent}
                                    </div>
                                    <div className='modal-footer'>
                                        { modalButtons.map(button => (
                                            <Button 
                                                title={button.title} 
                                                className={'modal-btn ' + button.className}
                                                style={{gridColumn: button.column}}
                                                onClick={button.onClick}
                                            />
                                        ))}
                                    </div>
                                </div> 
                            } 
                            <div className='notifications-container'>
                                {
                                    notifications.map(n => 
                                        <Notification key={n.id} {...n} />
                                    )
                                }
                            </div>
                            <Routes>
                                <Route path="/" element={<IndexPage />} />
                                <Route path="/register" element={<RegisterPage />} />
                                <Route path="/forums" element={<ForumIndex />} />
                                <Route path="/forums/private" element={<PrivatePage />} />
                                <Route path="/forums/private/:section" element={<PrivatePage />} />
                                <Route path="/forums/private/:section/:page" element={<PrivatePage />} />
                                <Route path="/forums/:forumId" element={<ForumPage />} />
                                <Route path="/forums/:forumId/new" element={<NewThread />} />
                                <Route path="/forums/threads/:threadId" element={<ThreadPage />} />
                                <Route path="/forums/threads/:threadId/:page" element={<ThreadPage />} />
                            </Routes>
                            <Footer />
                        </NotificationContext.Provider>
                    </UserContext.Provider>
                </div>
            </Router>
        </React.StrictMode>
    )
}
