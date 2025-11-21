import { createContext, useContext, useState, useEffect } from "react";
import {
  Modal,
  View,
  Text,
  TextInput,
  Pressable,
  Alert,
  Image,
} from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import styles from "./Style.jsx";

const AppContext = createContext();
const SERVER_URL = "https://testing.allydoes.tech";

export function AppProvider({ children }) {
  const [token, setToken] = useState(null);

  const [usernameDisplay, setUsernameDisplay] = useState("");
  const [budgets, setBudgets] = useState([]);
  const [connections, setConnections] = useState([]);
  const [balanceDollars, setBalanceDollars] = useState(0);
  const [balanceCents, setBalanceCents] = useState(0);

  const [authMode, setAuthMode] = useState("signup");
  const [showAuthModal, setShowAuthModal] = useState(true);

  const [usernameInput, setUsernameInput] = useState("");
  const [passwordInput, setPasswordInput] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  // ---------------------------
  // NEW: Local transfer history
  // ---------------------------
  const [transferHistory, setTransferHistory] = useState([]);

  async function loadTransferHistory() {
    const saved = await AsyncStorage.getItem("transferHistory");
    if (saved) setTransferHistory(JSON.parse(saved));
  }

  async function saveTransferLocally({ from, to, dollars, cents }) {
    const newEntry = {
      id: Date.now(),
      from,
      to,
      dollars,
      cents,
      timestamp: new Date().toISOString(),
    };

    const updated = [...transferHistory, newEntry];
    setTransferHistory(updated);

    await AsyncStorage.setItem("transferHistory", JSON.stringify(updated));
  }

  // Load token + state + transfer history on startup
  useEffect(() => {
    async function init() {
      const storedToken = await AsyncStorage.getItem("TOKEN");

      await loadTransferHistory();

      if (storedToken) {
        setToken(storedToken);
        await loadUserState(storedToken);
        setShowAuthModal(false);
      }
    }
    init();
  }, []);

  async function loadUserState(authToken) {
    try {
      const res = await fetch(
        `${SERVER_URL}/GetUserState?token=${encodeURIComponent(authToken)}`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );

      if (!res.ok) throw new Error("Failed to get user state.");

      const data = await res.json();

      setUsernameDisplay(data.username);
      setBudgets(data.budgets || []);
      setConnections(data.connections || []);
      setBalanceDollars(data.balanceDollars || 0);
      setBalanceCents(data.balanceCents || 0);
    } catch (err) {
      console.error(err);
      Alert.alert("Error", "Could not fetch account details.");
    }
  }

  async function handleSignup() {
    if (!usernameInput || !passwordInput || !confirmPassword) {
      Alert.alert("Missing Fields", "Please fill all fields.");
      return;
    }

    if (passwordInput !== confirmPassword) {
      Alert.alert("Error", "Passwords do not match.");
      return;
    }

    const res = await fetch(`${SERVER_URL}/RegisterUser`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: usernameInput, password: passwordInput }),
    });

    if (res.status === 403) {
      Alert.alert("Error", "Username already exists.");
      return;
    }

    handleLogin();
  }

  async function handleLogin() {
    if (!usernameInput || !passwordInput) {
      Alert.alert("Missing Fields", "Enter username and password.");
      return;
    }

    const res = await fetch(
      `${SERVER_URL}/Login?username=${encodeURIComponent(
        usernameInput
      )}&password=${encodeURIComponent(passwordInput)}`,
      {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      }
    );

    if (res.status === 403) {
      Alert.alert("Login Failed", "Incorrect username or password.");
      return;
    }

    const data = await res.json();
    await AsyncStorage.setItem("TOKEN", data.token);
    setToken(data.token);

    await loadUserState(data.token);
    setShowAuthModal(false);
  }

  async function logout() {
    if (!token) return;

    await fetch(`${SERVER_URL}/Logout`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ token }),
    });

    await AsyncStorage.removeItem("TOKEN");
    setToken(null);
    setShowAuthModal(true);
  }

  return (
    <AppContext.Provider
      value={{
        token,
        usernameDisplay,
        budgets,
        connections,
        balanceDollars,
        balanceCents,
        logout,
        loadUserState,
        transferHistory,
        saveTransferLocally,   // NEW
      }}
    >
      {children}

      {/* Auth Modal */}
      <Modal visible={showAuthModal} transparent={false}>
        <View style={styles.overlay}>
          <View style={styles.logoContainer}>
            <Image
              source={require("../../assets/images/icon.png")}
              style={styles.logo}
              resizeMode="contain"
            />
          </View>

          <View style={styles.formContainer}>
            <Text style={styles.title}>
              {authMode === "signup" ? "Create Account" : "Welcome Back"}
            </Text>

            <TextInput
              placeholder="Username"
              style={styles.input}
              autoCapitalize="none"
              value={usernameInput}
              onChangeText={setUsernameInput}
            />

            <TextInput
              placeholder="Password"
              style={styles.input}
              secureTextEntry
              value={passwordInput}
              onChangeText={setPasswordInput}
            />

            {authMode === "signup" && (
              <TextInput
                placeholder="Confirm Password"
                secureTextEntry
                style={styles.input}
                value={confirmPassword}
                onChangeText={setConfirmPassword}
              />
            )}
          </View>

          <View style={styles.footer}>
            <Pressable
              style={styles.authButton}
              onPress={authMode === "signup" ? handleSignup : handleLogin}
            >
              <Text style={styles.authButtonText}>
                {authMode === "signup" ? "Sign Up" : "Login"}
              </Text>
            </Pressable>

            <Text
              style={styles.switchText}
              onPress={() =>
                setAuthMode(authMode === "signup" ? "login" : "signup")
              }
            >
              {authMode === "signup"
                ? "Already have an account? Login"
                : "No account? Create one"}
            </Text>
          </View>
        </View>
      </Modal>
    </AppContext.Provider>
  );
}

export function useAppState() {
  return useContext(AppContext);
}
