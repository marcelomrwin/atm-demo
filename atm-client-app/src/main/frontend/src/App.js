import React from "react";
import { useEffect, useState } from "react";
import logo from './logo.svg';
import './App.css';

function App() {

    const [userLogged, setUserLogged] = useState({});

    useEffect(() => {
        fetch('/api/user/logged')
            .then(res => res.json())
            .then((data) => {
                setUserLogged(data)
            })
            .catch(console.log)
    }, [])

    const getUserName = () => {
        return userLogged.userName;
    }

    const isAuthenticated = () => {
        return userLogged != null && userLogged.userName != null;
    }

    const hasRole = (roles) => {
        return userLogged != null && userLogged.roles != null && roles.some(role => userLogged.roles.includes(role));
    }

    return (
        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo"/>
                {!isAuthenticated() && <p><h2>You are not authenticated</h2></p>}
                {
                    isAuthenticated() && <p>
                        <h2>Hello {getUserName()}!</h2>
                        {hasRole(['app_admin']) && <p>You have the App Admin Realm role, congratulations!</p>}
                        {!hasRole(['app_admin']) && <p>You don't have the App Admin Realm role, sorry!</p>}
                    </p>
                }
            </header>
        </div>
    );
}

export default App;
