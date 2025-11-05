import { createContext, useContext, useState } from "react";

const AppContext = createContext();

export function AppProvider({ children }) {
  const [name, setName] = useState("John Doe");
  const [money, setMoney] = useState(48104);
  const [budgets, setBudgets] = useState([]);

  return (
    <AppContext.Provider
      value={{
        name,
        setName,
        money,
        setMoney,
        budgets,
        setBudgets
      }}
    >
      {children}
    </AppContext.Provider>
  );
}

export function useAppState() {
  return useContext(AppContext);
}