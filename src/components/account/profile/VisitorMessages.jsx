import React, { useState, useEffect, useContext } from 'react';

import axios from '../../../utils/axios';
import { validate } from '../../../utils/validate';

import RichTextEditor from '../../../components/utils/editor/RichTextEditor';
import VisitorMessage from './VisitorMessage';

import Button from '../../../components/utils/Button';
import PageContext from '../../../utils/contexts/PageContext';
import NotificationContext from '../../../utils/contexts/NotificationContext';
import ViewingUserContext from '../../../utils/contexts/ViewingUserContext';
import UserContext from '../../../utils/contexts/UserContext';
import Pages from '../../../components/utils/Pages';

async function sendVisitorMessage(user, message, setMessage, setMessages, sendErrorNotification) {
	let [ validated, error ] = validate({
		message: {
			required: true,
			name: 'Visitor Message',
			min: 3,
			max: 100,
		}
	}, { message });

	if(!validated) {
		sendErrorNotification(error);
		return;
	}

	try {

		let res = await axios.post(`/users/${user._id}/messages`, { message });

		setMessage('');
		setMessages(messages => [ ...messages, res.data.message ]);

	} catch(error) {
		sendErrorNotification(error);
	}
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

			try {

				let res = await axios.get(`/users/${viewingUser._id}/messages/${page}`);

				setMessages(res.data.messages);
				setPageTotal(res.data.pageTotal);

			} catch(error) {
				sendErrorNotification(error);
			}

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
