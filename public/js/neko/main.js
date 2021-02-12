let connection;
let peer;
let channel;

let memberList;

let state;

let username;

let mousePosX;
let mousePosY;

let firstWarn = false;
let controlling = false;

let controller;

let id;

let users = [];

let video = createVideoObject(sendMessage, onStreamChange);
let storage = createStorageObject();
let chat = createChatObject(sendMessage, getUsername);

let rights;

const OPCODE = {
    MOVE: 0x01,
    SCROLL: 0x02,
    KEY_DOWN: 0x03,
    KEY_UP: 0x04
};

function createConnection(user, sessionId, userRights) {
    connection = new WebSocket('ws://neko.cryogen-rsps.com/ws?sessionId=' + sessionId);
    connection.onopen = connectionOpen;
    connection.onmessage = onMessage;
    connection.onerror = onError;
    username = user;
    rights = userRights;
    //TODO - when connecting to neko via GET, generate a server key with username attached to it
    //Pass the key back to website here, that's what we'll use to authenticate with the websocket.
    //Websocket can get our names and shit from there
    //For now, just use usernames
}

function connectionOpen(event) {
    console.log('Connection has been created.', event);
}

function onMessage(event) {
    let data = JSON.parse(event.data);
    switch (data.event) {
        case 'signal/provide':
            let { sdp, lite, ice, id } = data;
            peer = new RTCPeerConnection();
            if (lite !== true) {
                peer = new RTCPeerConnection({
                    iceServers: [{ urls: ice || [] }]
                });
            }
            listenForEvents(sdp);
            break;
        case 'screen/resolution':
            let width = data.width;
            let height = data.height;
            console.log('SETTING RESOLUTION:', width, height);
            break;
        case 'member/list':
            memberList = data.members;
            updateMemberList();
            break;
        case 'member/connected':
            let userId = data.id;
            let name = data.displayname;
            users[userId] = name;
            if (memberList.filter(m => m.displayname == name).length > 0)
                return;
            memberList.push({
                userId,
                displayname: name,
                muted: false,
                admin: false
            });
            updateMemberList();
            break;
        case 'control/locked':
            if (rights == 2) {
                //if it is us, show disable controls, if not, show enable controls
                sendAlert(data.name + ' has taken control!');
                let controlling = data.name == username;
                $('#neko-controlling').find('span').html(' ' + (controlling ? 'Disable' : 'Enable') + ' Controls');
            }
            break;
        case 'control/release':
            if (rights == 2)
                sendAlert(data.name + ' has given up control!');
            break;
        case 'chat/message':
            chat.receiveMessage(data);
            break;
        case 'system/disconnect':
            sendAlert('Disconnected from websocket. Reason: ' + data.message);
            console.error(data.error);
            break;
        default:
            console.log('UNCONDITIONED MESSAGE', data);
            break;
    }
}

function listenForEvents(sdp) {
    peer.onconnectionstatechange = event => {
        console.log('Peer connection state changed:', peer ? peer.connectionState : undefined);
    };

    peer.onsignalingstatechange = event => {
        console.log('Peer signaling state changed:', peer ? peer.signalingState : undefined);
    };

    peer.oniceconnectionstatechange = event => {

        state = peer.iceConnectionState;

        console.log('Peer ICE connection state changed:', state);

        switch (state) {
            case 'checking':
                break;
            case 'connected':
                onConnected();
                break;
            case 'failed':
                onDisconnected('Peer failed');
                break;
            case 'disconnected':
                onDisconnected('Peer disconnected');
                break;
        }
    };

    peer.ontrack = onTrack;
    peer.addTransceiver('audio', { direction: 'recvonly' });
    peer.addTransceiver('video', { direction: 'recvonly' });

    channel = peer.createDataChannel('data');
    channel.onerror = onError;
    channel.onmessage = onChannelData;
    channel.onclose = () => onDisconnected("Peer data channel disconnected.");

    peer.setRemoteDescription({ type: 'offer', sdp });

    peer
        .createAnswer()
        .then(d => {
            peer.setLocalDescription(d);
            connection.send(
                JSON.stringify({
                    event: 'signal/answer',
                    sdp: d.sdp
                })
            );
        })
        .catch(err => console.error(err));

}

function sendMessage(event, payload) {
    console.log('SENDING MESSAGE', event, payload);

    if (!connection) return;
    connection.send(JSON.stringify({ event, ...payload }));
}

function sendData(event, data) {
    let buffer;
    let payload;
    switch (event) {
        case 'mousemove':
            buffer = new ArrayBuffer(7);
            payload = new DataView(buffer);
            payload.setUint8(0, OPCODE.MOVE);
            payload.setUint16(1, 4, true);
            payload.setUint16(3, data.x, true);
            payload.setUint16(5, data.y, true);
            break;
        case 'wheel':
            buffer = new ArrayBuffer(7)
            payload = new DataView(buffer)
            payload.setUint8(0, OPCODE.SCROLL)
            payload.setUint16(1, 4, true)
            payload.setInt16(3, data.x, true)
            payload.setInt16(5, data.y, true)
            break
        case 'keydown':
        case 'mousedown':
            buffer = new ArrayBuffer(11)
            payload = new DataView(buffer)
            payload.setUint8(0, OPCODE.KEY_DOWN)
            payload.setUint16(1, 8, true)
            payload.setBigUint64(3, BigInt(data.key), true)
            break
        case 'keyup':
        case 'mouseup':
            buffer = new ArrayBuffer(11)
            payload = new DataView(buffer)
            payload.setUint8(0, OPCODE.KEY_UP)
            payload.setUint16(1, 8, true)
            payload.setBigUint64(3, BigInt(data.key), true)
            break
        default:
            console.error('ERROR SENDING DATA', event, data);
            break;
    }
    if (typeof buffer !== 'undefined')
        channel.send(buffer);
}

function onChannelData(event) {
    //uhh? doesn't do anything?
    console.log('RECEIVED DATA:', event);
}

function onConnected(event) {
    sendAlert('Successfully connected to Neko.');
}

function onDisconnected(message) {
    message = message || 'Disconnected from Neko';
    sendAlert(message);
}

function onTrack(event) {
    console.log(`Received ${event.track.kind} track from peer: ${event.track.id}`, event);

    let stream = event.streams[0];
    if (!stream) {
        console.error(`No stream provided for track ${event.track.id}(${event.track.label})`);
        return false;
    }

    if (event.track.kind === 'audio') return; //? Where do we get audio streams from then?

    video.addTrack([event.track, stream]);
    video.setStream(0);

}

function onError(event) {
    console.error('Error recieved', event.error || event);
}

function updateMemberList() {
    post('/neko/member-list', { members: JSON.stringify(memberList) }, '#neko-members');
}

function getUsername(id) {
    return users[id];
}

function onVolumeChange() {
    let volume = $('.volume-slider').val() / 100;

    setVolume(volume);
}

function setVolume(volume) {
    let element = getVideoElement();
    if (!element) return;

    element.volume = volume;
}

function toggleMute() {
    let element = getVideoElement();
    if (!element) return;

    element.muted = !element.muted;

    let muted = element.muted;

    element = $('#neko-mute');
    element.find('i').attr('class', 'fas fa-volume' + (!muted ? '-mute' : '-up'));
    element.find('span').html(' ' + (muted ? 'Unmute' : 'Mute'));
}

function onStreamChange() {
    let element = getVideoElement();
    if (!element || !video.getStream()) return;
    console.log(video.getStream());
    if ('srcObject' in element) {
        element.srcObject = video.getStream();
    } else {
        element.src = URL.createObjectURL(video.getStream());
    }
    firstPlay();
}

function toggleControl() {
    let opcode = controlling ? 'control/release' : 'control/request';
    controlling = !controlling;
    sendMessage(opcode);
    $('#neko-controlling').find('span').html(' ' + (controlling ? 'Disable' : 'Enable') + ' Controls');
    return false;
}

function sendMousePosition(event) {
    if (!controlling) return;
    let { w, h } = video.getResolution();
    let rect = $('.overlay')[0].getBoundingClientRect();

    mousePosX = event.pageX;
    mousePosY = event.pageY;


    if (mousePosX > rect.x && mousePosX < (rect.x + rect.width) &&
        mousePosY > rect.y && mousePosY < (rect.y + rect.height)) {

        let x = Math.round((w / rect.width) * (event.clientX - rect.left));
        let y = Math.round((h / rect.height) * (event.clientY - rect.top));

        sendData('mousemove', { x, y });
    }
}

function onMouseDown(event) {
    if (!controlling) return;
    sendMousePosition(event);

    sendData('mousedown', { key: event.button + 1 });


    return false;

}

function onMouseUp(event) {
    if (!controlling) return;
    sendMousePosition(event);

    sendData('mouseup', { key: event.button + 1 });

    return false;
}

function sendMouseWheel(event) {
    if (!controlling) return;
    sendMousePosition(event);

    //not working?

    let x = event.deltaX;
    let y = event.deltaY;

    let scroll = storage.getSetting('scroll');

    x = Math.min(Math.max(x, -scroll), scroll);
    y = Math.min(Math.max(y, -scroll), scroll);

    sendData('wheel', { x, y });
    return false;
}

function onMouseEnter(event) {
    if (controlling) {
        syncKeyboardModifierState({
            capsLock: event.originalEvent.getModifierState("CapsLock"),
            numLock: event.originalEvent.getModifierState("NumLock"),
            scrollLock: event.originalEvent.getModifierState("ScrollLock")
        });
    }

    $('.overlay').focus();
    return false;
}

function onMouseLeave(event) {
    if (controlling) {
        setKeyboardModifierState({
            capsLock: event.originalEvent.getModifierState("CapsLock"),
            numLock: event.originalEvent.getModifierState("NumLock"),
            scrollLock: event.originalEvent.getModifierState("ScrollLock")
        });
    }
    return false;
}

function setKeyboardModifierState(options) {

    let state = storage.getSetting('keyboard-state');

    let newState = keyboardModifierState(options.capsLock, options.numLock, options.scrollLock);

    if (state == newState) return;

    storage.setSetting('keyboard-state', newState);
    return false;
}

function syncKeyboardModifierState(options) {
    // setKeyboardModifierState(options);
    // sendMessage('control/keyboard', options);
}

const keyboardModifierState = (caps, num, scroll) =>
    (Number(caps) + (2 * Number(num)) + (4 * Number(scroll)));

function firstPlay() {
    getVideoElement().play()
        .then(() => {
            video.play();
        })
        .catch(err => {
            if (!firstWarn) {
                sendAlert('The stream will start displaying once you interact with the page.');
                firstWarn = true;
            }
            setTimeout(firstPlay, 500);
        });
}

function toggleFullscreen() {
    $('#neko-content')[0].requestFullscreen();
}

function getVideoElement() {
    return $('video')[0];
}

$(document).ready(() => {

    onStreamChange();

    setVolume(.5);

    $(document).mousemove(sendMousePosition);
    $('.overlay').mouseenter(onMouseEnter);
    $('.overlay').mouseleave(onMouseLeave);
    $('.overlay').mousedown(onMouseDown);
    $('.overlay').mouseup(onMouseUp);

    $('#neko-controlling').click(toggleControl);
    $('#neko-fullscreen').click(toggleFullscreen);
    $('#neko-mute').click(toggleMute);
    $('#neko-volume').find('.volume-slider').change(onVolumeChange);

    chat.load();

    $(document).keydown((event) => {
        let rect = $('.overlay')[0].getBoundingClientRect();
        if (mousePosX > rect.x && mousePosX < (rect.x + rect.width) &&
            mousePosY > rect.y && mousePosY < (rect.y + rect.height)) {
            sendData('keydown', { key: event.which });
        }
    });

    $(document).keyup((event) => {
        let rect = $('.overlay')[0].getBoundingClientRect();
        if (mousePosX > rect.x && mousePosX < (rect.x + rect.width) &&
            mousePosY > rect.y && mousePosY < (rect.y + rect.height)) {
            sendData('keyup', { key: event.which });
        }
    });

});