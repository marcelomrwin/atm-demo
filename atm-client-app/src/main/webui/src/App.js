import React, {useState, useEffect} from "react";

import {Avatar} from 'primereact/avatar';
import {Toolbar} from 'primereact/toolbar';
import {Image} from 'primereact/image';
import {Divider} from 'primereact/divider';
import {Splitter, SplitterPanel} from 'primereact/splitter';
import {PickList} from 'primereact/picklist';
import {Button} from 'primereact/button';
import {Timeline} from 'primereact/timeline';
import {Card} from 'primereact/card';

// import 'primereact/resources/themes/saga-blue/theme.css';
import "primereact/resources/themes/lara-light-cyan/theme.css";
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';

import {PrimeReactProvider, PrimeReactContext} from 'primereact/api';

function App() {

    const [userLogged, setUserLogged] = useState({});
    const [source, setSource] = useState([]);
    const [target, setTarget] = useState([]);
    const [socket, setSocket] = useState([]);
    const [connected, setConnected] = useState(false);
    const [events, setEvents] = useState([]);
    const [topic, setTopic] = useState([]);
    const WS_URL = "ws://" + window.location.host + "/sse/";

    var ws = null;

    useEffect(() => {
        fetch('/api/user/logged')
            .then(res => res.json())
            .then((data) => {
                setUserLogged(data);

                if (!connected)
                    configureWebSocket(data);

            })
            .catch(console.log)
    }, [])

    useEffect(() => {
        fetch('/api/topics')
            .then(res => res.json())
            .then((data) => {
                setSource(data)
            })
            .catch(console.log)
    }, [])

    const configureWebSocket = (data) => {

        ws = new WebSocket(WS_URL + data.userName);
        setSocket(ws);
        setConnected(true);

        ws.addEventListener("onopen", event => {
            ws.send("Connection established")
        });

        ws.addEventListener("message", event => {
            console.log("Message from server ", event.data);
            let arrivalList = JSON.parse(event.data);
            console.log(arrivalList);
            setEvents(arrivalList.entries);
            setTopic(arrivalList.topic);
        });

        ws.addEventListener("close", event => {
            console.log("Websocket Connection lost! Reconnecting");
            setTimeout(function () {
                configureWebSocket();
            }, 1000)
        })
    }

    const onChange = (event) => {
        setSource(event.source);
        setTarget(event.target);
    };

    const getUserName = () => {
        return userLogged.userName;
    }

    const getAppName = () => {
        return userLogged.applicationClient;
    }

    const isAuthenticated = () => {
        return userLogged != null && userLogged.userName != null;
    }

    const hasRole = (roles) => {
        return userLogged != null && userLogged.roles != null && roles.some(role => userLogged.roles.includes(role));
    }

    const startContent = (
        <React.Fragment>
            <div className="flex align-items-center gap-2">
                <Image src="/Logo-Red_Hat-A-Standard-RGB.Small-logo-transparent.png" alt="Red Hat Log" width="200"/>
                <Divider layout="vertical"/>
                <span className="font-bold text-bluegray-50">{getAppName()}</span>
            </div>
        </React.Fragment>
    );

    const centerContent = (
        <div className="flex flex-wrap align-items-center gap-3">
            <button
                className="p-link inline-flex justify-content-center align-items-center text-white h-3rem w-3rem border-circle hover:bg-white-alpha-10 transition-all transition-duration-200">
                <i className="pi pi-home text-2xl"></i>
            </button>
            <button
                className="p-link inline-flex justify-content-center align-items-center text-white h-3rem w-3rem border-circle hover:bg-white-alpha-10 transition-all transition-duration-200">
                <i className="pi pi-user text-2xl"></i>
            </button>
            <button
                className="p-link inline-flex justify-content-center align-items-center text-white h-3rem w-3rem border-circle hover:bg-white-alpha-10 transition-all transition-duration-200">
                <i className="pi pi-search text-2xl"></i>
            </button>
        </div>
    );

    const endContent = (
        <React.Fragment>
            <div className="flex align-items-center gap-2">
                <Avatar image="https://primefaces.org/cdn/primereact/images/organization/walter.jpg" shape="circle"/>
                <span className="font-bold text-bluegray-50">Hello {getUserName()}</span>
            </div>
        </React.Fragment>
    );

    const itemTemplate = (item) => {
        return (
            <div className="flex flex-wrap p-2 align-items-center gap-3">
                <span className="font-bold">{item.name}</span>
            </div>
        );
    };

    const cardHeader = (
        <img alt="Card" src="/air-traffic.jpg" height={150}/>
    );

    const customizedContent = (item) => {
        return (
            <Card title={item.arcid} subTitle={item.eobt}>
                {/*<img src="/airplane-arrival.png" alt={item.arcid} width={100} height={50} className="shadow-1" />*/}
                <p>{item.arctyp}</p>
                {/*<Button label="Detail" className="p-button-text"></Button>*/}
            </Card>
        );
    };

    return (
        <div className="App">
            <header className="App-header">
                {!isAuthenticated() && <h2>You are not authenticated</h2>}
                {
                    isAuthenticated() &&
                    <div className="card">
                        <Toolbar start={startContent} center={centerContent} end={endContent}
                                 className="bg-gray-900 shadow-2" style={{
                            borderRadius: '3rem',
                            backgroundImage: 'linear-gradient(to right, var(--bluegray-500), var(--bluegray-800))'
                        }}/>
                    </div>
                }
            </header>
            <Splitter style={{height: '100%'}}>
                <SplitterPanel className="flex align-items-center justify-content-center" size={40} minSize={20}>

                    <div className="card align-items-center justify-content-center">
                        <PickList dataKey="id" source={source} target={target} onChange={onChange}
                                  itemTemplate={itemTemplate} breakpoint="1280px"
                                  sourceHeader="Topics Available" targetHeader="Topics Selected"
                                  sourceStyle={{height: '24rem'}}
                                  targetStyle={{height: '24rem'}}/>
                        <Divider/>
                        <Button label="Subscribe" icon="pi pi-check"/>
                    </div>
                </SplitterPanel>
                <SplitterPanel className="flex align-items-center justify-content-center">

                    <div className="card flex justify-content-center">

                        <Card title={topic} subTitle="Arrival order of flights" header={cardHeader}
                              className="md:w-25rem">

                            <Timeline value={events} opposite={(item) => item.arcid}
                                      content={(item) => <small
                                          className="text-color-secondary">{item.eobt} {item.arctyp}</small>} align="alternate"/>

                        </Card>

                    </div>

                </SplitterPanel>
            </Splitter>

            <div className="bottom-nav">
                {hasRole(['app_admin']) && <p>You have the App Admin Realm role, congratulations!</p>}
                {!hasRole(['app_admin']) && <p>You don't have the App Admin Realm role, sorry!</p>}
            </div>
        </div>
    );
}

export default function MyApp({
                                  Component, pageProps
                              }) {
    return (
        <PrimeReactProvider>
            <App/>
        </PrimeReactProvider>
    );
}
