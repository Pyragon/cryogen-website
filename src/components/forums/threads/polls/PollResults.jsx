import React from 'react'
import ProgressBar from '../../../utils/ProgressBar';

import axios from '../../../../utils/axios';
import Poll from '../../../../utils/poll';

export default function PollResults({ poll, setPoll, showResults, setShowResults }) {
    let removeVote = async() => {
        if(showResults) {
            console.log('setting');
            setShowResults(false);
            return;
        }
        if(!poll.allowVoteChange) {
            console.error('You cannot remove your vote from this poll!');
            return;
        }
        let res = await axios.post('/forums/threads/polls/removeVote', {
            poll: poll._id
        });
        if(res.data.error) {
            console.error(res.data.error);
            return;
        }
        setPoll(new Poll(res.data.poll));
    };
    return (
        <>
            { poll.answers.map((answer, index) => {
                let totalVotes = poll.votes.length;
                let answerVotes = poll.votes.filter(vote => vote.index === index).length;
                let percentage = (answerVotes / totalVotes) * 100;
                if(isNaN(percentage))
                    percentage = 0;
                return (
                    <div key={index}>
                        <p className="input-title-p">{answer+' - '+percentage+'% ('+answerVotes+' votes)' }</p>
                        <ProgressBar bgcolor='#124d70' completed={percentage} />
                    </div>
                )
            })}
            { poll.allowVoteChange && 
                <div className="poll-config-container">
                    <div className="poll-change-vote" onClick={removeVote}>Change Vote</div>
                </div>
            }
        </>
    );
}
