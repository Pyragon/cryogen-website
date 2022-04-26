import React, { useContext, useState } from 'react';

import _Poll from '../../../../utils/poll';
import Widget from '../../../utils/Widget';
import PollVoting from './PollVoting';
import PollResults from './PollResults';

import UserContext from '../../../../utils/contexts/UserContext';
import NotificationContext from '../../../../utils/contexts/NotificationContext';

export default function Poll({ thread }) {
    let { user } = useContext(UserContext);
    let [ poll, setPoll ] = useState(new _Poll(thread.poll));

    let [ showResults, setShowResults ] = useState(false);

    let { sendErrorNotification } = useContext(NotificationContext);

    let voteOnOption = async(index) => {
        if(user === null) {
            sendErrorNotification('You must be logged in to vote!');
            return;
        }
        if(poll.hasVoted(user) && !poll.allowVoteChange) {
            sendErrorNotification('You have already voted on this poll and it does not allow you to change your vote!');
            return;
        }
        setPoll(await poll.vote(thread.poll, index));
    };
    return (
        <Widget 
            title={thread.poll.question} 
            style={{paddingBottom: '10px'}} 
            descriptions={[
                "The thread author has posted this poll to vote on",
                "Total votes: " + poll.votes.length
            ]}
        >
            { (showResults || (user && poll.hasVoted(user))) && <PollResults poll={poll} setPoll={setPoll} showResults={showResults} setShowResults={setShowResults} /> }
            { !showResults && user && !poll.hasVoted(user) && <PollVoting poll={poll} voteOnOption={voteOnOption} setShowResults={setShowResults} /> }
            { !showResults && !user && !poll.showResultsBeforeVote && <p>You must be logged in to vote on or view the results for this poll.</p> }
            { !showResults && !user && poll.showResultsBeforeVote && <PollResults poll={poll} setPoll={setPoll} showResults={showResults} setShowResults={setShowResults} /> }
        </Widget>
    )
}
