import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

import PageContext from '../utils/contexts/PageContext';
import SectionContext from '../utils/contexts/SectionContext';

import BeastOfBurdenLogs from '../components/logs/BeastOfBurdenLogs';
import BeastOfBurdenDeathLogs from '../components/logs/BeastOfBurdenDeathLogs';
import ChatLogs from '../components/logs/ChatLogs';
import CommandLogs from '../components/logs/CommandLogs';
import DeathLogs from '../components/logs/DeathLogs';
import DicingLogs from '../components/logs/DicingLogs';
import DropLogs from '../components/logs/DropLogs';
import DuellingLogs from '../components/logs/DuellingLogs';
import GrandExchangeLogs from '../components/logs/GrandExchangeLogs';
import LoginLogs from '../components/logs/LoginLogs';
import NPCDropLogs from '../components/logs/NPCDropLogs';
import PickupLogs from '../components/logs/PickupLogs';
import PlayerOwnedShopLogs from '../components/logs/PlayerOwnedShopLogs';
import PvPLogs from '../components/logs/PvPLogs';
import ShopLogs from '../components/logs/ShopLogs';
import TradeLogs from '../components/logs/TradeLogs';

import Sections from '../components/utils/sections/Sections';

import '../styles/logs/LogPage.css';

export default function LogPage() {
    let { section: sectionParam, page: pageParam } = useParams();

    let setSection = (newSection) => {
        newSection = sections.find(s => s.title.toLowerCase() === newSection.toLowerCase());
        if(!newSection)
            newSection = sections[0];
        
        setSectionState(newSection);
    };

    let [ page, setPage ] = useState(Number(pageParam) || 1);
    let [ section, setSectionState ] = useState(null);
    let [ sectionTitle, setSectionTitle ] = useState(null);
    let [ sectionDescription, setSectionDescription ] = useState(null);

    useEffect(() => {
        if(!section)
            return;
        let p = '/'+page.toString();
        window.history.replaceState(null, '', '/staff/logs/'+section.title.toLowerCase().replaceAll(' ', '_')+p);
    }, [ section, page ]);

    useEffect(() => {

        let active = !sectionParam ? sections[0] : sections.find(section => section.title.toLowerCase().replaceAll(' ', '_') === sectionParam.toLowerCase());
        if(!active)
            active = sections[0];
        setSectionState(active);
    }, []);

    return (
        <SectionContext.Provider value={{section, setSection, sectionTitle, setSectionTitle, sectionDescription, setSectionDescription }}>
            <PageContext.Provider value={{page, setPage}}>
                { section && 
                    <Sections 
                        sections={sections}
                        active={section}
                    /> 
                }
            </PageContext.Provider>
        </SectionContext.Provider>
    );
}

const sections = [
    {
        title: 'Beast of Burden',
        content: <BeastOfBurdenLogs />,
        description: 'Logs of all transfers to and from beast of burden familiars.',
    },
    {
        title: 'Beast of Burden Death',
        content: <BeastOfBurdenDeathLogs />,
        description: 'Logs of all items dropped from beast of burden familiars dying.',
    },
    {
        title: 'Chat',
        content: <ChatLogs />,
        description: 'Logs of all chat messages.',
    },
    {
        title: 'Commands',
        content: <CommandLogs />,
        description: 'Logs of all commands used by players.',
    },
    {
        title: 'Deaths',
        content: <DeathLogs />,
        description: 'Logs of all deaths.',
    },
    {
        title: 'Dicing',
        content: <DicingLogs />,
        description: 'Logs of all dicing wins or losses.',
    },
    {
        title: 'Drop',
        content: <DropLogs />,
        description: 'Logs of all items dropped by players.',
    },
    {
        title: 'Duelling',
        content: <DuellingLogs />,
        description: 'Logs of all duelling wins or losses.',
    },
    {
        title: 'Grand Exchange',
        content: <GrandExchangeLogs />,
        description: 'Logs of all transactions in the grand exchange.',
    },
    {
        title: 'Login',
        content: <LoginLogs />,
        description: 'Logs of all logins.',
    },
    {
        title: 'NPC Drops',
        content: <NPCDropLogs />,
        description: 'Logs of all drops from NPCs.',
    },
    {
        title: 'Pickup',
        content: <PickupLogs />,
        description: 'Logs of all items picked up by players.',
    },
    {
        title: 'Player Owned Shop',
        content: <PlayerOwnedShopLogs />,
        description: 'Logs of all transactions in the player owned shop.',
    },
    {
        title: 'PvP',
        content: <PvPLogs />,
        description: 'Logs of all PvP wins and losses.',
    },
    {
        title: 'Shop',
        content: <ShopLogs />,
        description: 'Logs of all transactions in shops.',
    },
    {
        title: 'Trade',
        content: <TradeLogs />,
        description: 'Logs of all trades between players.',
    }
];
