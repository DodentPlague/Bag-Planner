import { createContext, useContext, useState, useEffect } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";

const AppContext = createContext();

const SERVER_URL = "http://localhost:8080"; 

export function AppProvider({ children }) {
  const [userId, setUserId] = useState(null);
  const [name, setName] = useState("");
  const [money, setMoney] = useState(0);
  const [budgets, setBudgets] = useState([]);

  useEffect(() => {
    async function init() {
      const id = await getOrCreateUserId();
      setUserId(id);

      const res = await fetch(buildURL("/getUserState", { userId: id }));
      const data = await res.json();

      setName(data.name);
      setMoney(data.money);
      setBudgets(data.budgets);
    }

    init();
  }, []);

  async function saveState() {
    if (!userId) return;

    await fetch(buildURL("/setUserState", { userId }), {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, money, budgets }),
    });
  }

  return (
    <AppContext.Provider
      value={{
        userId,
        name, setName,
        money, setMoney,
        budgets, setBudgets,
        saveState,
      }}
    >
      {children} 
    </AppContext.Provider>
  );
}

async function getOrCreateUserId() {
  let id = await AsyncStorage.getItem("USER_ID");

  if (!id) {
    const res = await fetch(buildURL("/register"));
    const data = await res.json();
    id = data.userId;
    await AsyncStorage.setItem("USER_ID", id);
  }

  return id;
}

function buildURL(endpoint, params = {}) {
  const url = new URL(endpoint, SERVER_URL);
  Object.entries(params).forEach(([key, value]) =>
    url.searchParams.append(key, value)
  );
  return url.toString();
}

export function useAppState() {
  return useContext(AppContext);
}

// meow