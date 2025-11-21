import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";

import { AppProvider } from "./constants/UserState";
import HomeScreen from "./screens/HomeScreen";
import ConnectionsScreen from "./screens/Connections.jsx";
import ConnectionProfileScreen from "./screens/ConnectionProfileScreen.jsx";

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <AppProvider>
      <NavigationContainer>
        <Stack.Navigator screenOptions={{ headerShown: false }}>
          <Stack.Screen name="Home" component={HomeScreen} />
          <Stack.Screen name="Connections" component={ConnectionsScreen} />
          <Stack.Screen name="ConnectionProfile" component={ConnectionProfileScreen} />
        </Stack.Navigator>
      </NavigationContainer>
    </AppProvider>
  );
}
