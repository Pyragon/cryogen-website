import React, { useState, useEffect, useContext } from 'react';

import axios from '../../../utils/axios';

import RichTextEditor from '../../../components/utils/editor/RichTextEditor';
import VisitorMessage from './VisitorMessage';

import Button from '../../../components/utils/Button';
import PageContext from '../../../utils/contexts/PageContext';
import NotificationContext from '../../../utils/contexts/NotificationContext';
import ViewingUserContext from '../../../utils/contexts/ViewingUserContext';
import UserContext from '../../../utils/contexts/UserContext';
import Pages from '../../../components/utils/Pages';

async function sendVisitorMessage(user, message, setMessage, setMessages, sendErrorNotification) {
	if(message.length < 5 || message.length > 200) {
		sendErrorNotification('Message must be between 5 and 200 characters.');
		return;
	}

	let res = await axios.post('/users/'+user._id+'/messages', {
		message
	});

	if(res.data.error) {
		sendErrorNotification(res.data.error);
		return;
	}

	setMessage('');
	setMessages(messages => [ ...messages, res.data.message ]);
}

export default function VisitorMessages() {
	let [ message, setMessage ] = useState('');

	let [ messages, setMessages ] = useState([]);
	let [ page, setPage ] = useState(1);
	let [ pageTotal, setPageTotal ] = useState(1);

	let { sendErrorNotification } = useContext(NotificationContext);
	let { viewingUser } = useContext(ViewingUserContext);
	let { user } = useContext(UserContext);

	useEffect(() => {

		let loadMessages = async() => {

			let res = await axios.get('/users/'+viewingUser._id+'/messages/'+page);
			if(res.data.error) {
				sendErrorNotification(res.data.error);
				return;
			}

			setMessages(res.data.messages);
			setPageTotal(res.data.pageTotal);

		};

		loadMessages();

	}, [ page ]);
	return (
		<>
			{ messages.length === 0 && 
				<p
					style={{ textAlign: 'center', marginBottom: '20px'}}
				>
					This user has not received any visitor messages. 
					{ user && <span> Be the first to post one!</span> }
				</p>
			}
			{ messages.length > 0 &&
				<PageContext.Provider value = {{ page, setPage }}>
					{ messages.map((message, index) => 
						<VisitorMessage key={index} message={ message } setMessages={setMessages} />
					)}
					<Pages
						pageTotal={pageTotal}
					/>
				</PageContext.Provider>
			}
			{ user && <>
				<RichTextEditor value={message} setState={setMessage} />
				<div className='send-visitor-message-btn-container'>
					<Button title='Send Message' onClick={() => sendVisitorMessage(viewingUser, message, setMessage, setMessages, sendErrorNotification)} />
				</div>
			</> }
		</>
	)
}
