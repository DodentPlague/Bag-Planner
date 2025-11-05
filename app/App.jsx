import { AppProvider } from "./constants/UserState";
import HomeScreen from "./screens/HomeScreen";

export default function App() {
  return (
    <AppProvider>
        <HomeScreen></HomeScreen>
    </AppProvider>
  );
}