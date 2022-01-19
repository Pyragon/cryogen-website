import React, { useContext } from 'react';

import { useNavigate } from 'react-router-dom';

import UserContext from '../../utils/contexts/UserContext';

import Button from '../utils/Button';

import '../../styles/index/IndexButtons.css'

export default function IndexButtons() {
    let { user } = useContext(UserContext);
    let loggedIn = user !== null;
    let navigate = useNavigate();
    return (
        <div className="index-btns">
            { !loggedIn && 
                <Button 
                    className="index-btn" 
                    title="Create Account" 
                    onClick={() => navigate('/register')} 
                />
            }
            <Button className="index-btn" title="Download Cryogen" />
        </div>
    )
}
