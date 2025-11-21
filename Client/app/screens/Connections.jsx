import { useState } from "react";
import {
  View,
  Text,
  TextInput,
  ScrollView,
  TouchableOpacity,
  Modal
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import { useAppState } from "../constants/UserState";
import styles from "../constants/Style";

export default function ConnectionsScreen() {
  const navigation = useNavigation();
  const { connections, token, refreshUserState } = useAppState();

  const [search, setSearch] = useState("");
  const [showAddModal, setShowAddModal] = useState(false);
  const [usernameToAdd, setUsernameToAdd] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  const filtered = connections.filter((name) =>
    name.toLowerCase().includes(search.toLowerCase())
  );

  const noConnections = connections.length === 0;
  const noSearchResults = filtered.length === 0 && connections.length > 0;

  // Add connection request
  async function handleAddConnection() {
    setErrorMsg("");

    if (usernameToAdd.trim() === "") {
      setErrorMsg("Enter a username.");
      return;
    }

    try {
      await fetch("https://testing.allydoes.tech/AddConnection", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          token: token,
          connectionUsername: usernameToAdd.trim(),
        }),
      }).then((res) => {
        console.log("Add Connection Response Code:", res.status);
      });

      // Refresh user state to show updated list
      await refreshUserState();

      setUsernameToAdd("");
      setShowAddModal(false);

    } catch (err) {
      setErrorMsg("Error adding connection.");
    }
  }

  return (
    <View style={styles.connectionsContainer}>

      {/* Back Button */}
      <TouchableOpacity
        style={styles.connectionsBackBtn}
        onPress={() => navigation.goBack()}
      >
        <Ionicons name="arrow-back" size={28} color="#000" />
      </TouchableOpacity>

      {/* Add Connection Button */}
      <TouchableOpacity
        style={styles.connectionsAddBtn}
        onPress={() => setShowAddModal(true)}
      >
        <Ionicons name="add" size={32} color="#000" />
      </TouchableOpacity>

      {/* Page Title */}
      <Text style={styles.connectionsPageTitle}>Connections</Text>

      {/* Search */}
      <TextInput
        placeholder="Search"
        value={search}
        onChangeText={setSearch}
        style={styles.connectionsSearch}
      />

      <ScrollView contentContainerStyle={styles.connectionsList}>

        {/* ---- NO CONNECTIONS AT ALL ---- */}
        {noConnections && (
          <Text style={styles.connectionsEmptyText}>
            You don't have any connections yet.
          </Text>
        )}

        {/* ---- NO SEARCH RESULTS ---- */}
        {noSearchResults && (
          <Text style={styles.connectionsEmptyText}>
            No results found.
          </Text>
        )}

        {/* ---- NORMAL LIST ---- */}
        {!noConnections && !noSearchResults && (
          <>
            <Text style={styles.connectionsSectionTitle}>Recents</Text>

            <View style={styles.connectionsRow}>
              {filtered.slice(0, 4).map((u, idx) => (
                <TouchableOpacity
                  key={idx}
                  style={styles.connectionsItem}
                  onPress={() =>
                    navigation.navigate("ConnectionProfile", { username: u })
                  }
                >
                  <View style={styles.connectionsCircle}>
                    <Text style={styles.connectionsInitial}>
                      {u[0].toUpperCase()}
                    </Text>
                  </View>
                </TouchableOpacity>
              ))}
            </View>

            <Text style={styles.connectionsSectionTitle}>Earlier</Text>

            {filtered.slice(4).map((u, idx) => (
              <View key={idx} style={styles.connectionsEntry}>

                <View style={styles.connectionsCircleLarge}>
                  <Text style={styles.connectionsInitialLarge}>
                    {u[0].toUpperCase()}
                  </Text>
                </View>

                <TouchableOpacity
                  style={styles.connectionsArrowBtn}
                  onPress={() =>
                    navigation.navigate("ConnectionProfile", { username: u })
                  }
                >
                  <Ionicons name="arrow-forward" size={26} color="#4A9BFF" />
                </TouchableOpacity>

              </View>
            ))}
          </>
        )}
      </ScrollView>

      {/* --- Add Connection Modal --- */}
      <Modal visible={showAddModal} transparent animationType="slide">
        <View style={styles.connectionsModalOverlay}>
          <View style={styles.connectionsModalBox}>

            <Text style={styles.connectionsModalTitle}>Add Connection</Text>

            <TextInput
              placeholder="Username"
              value={usernameToAdd}
              onChangeText={setUsernameToAdd}
              style={styles.connectionsModalInput}
            />

            {errorMsg ? (
              <Text style={styles.connectionsModalError}>{errorMsg}</Text>
            ) : null}

            <TouchableOpacity
              style={styles.connectionsModalAddBtn}
              onPress={handleAddConnection}
            >
              <Text style={styles.connectionsModalAddText}>Add</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.connectionsModalCancelBtn}
              onPress={() => {
                setShowAddModal(false);
                setUsernameToAdd("");
                setErrorMsg("");
              }}
            >
              <Text style={styles.connectionsModalCancelText}>Cancel</Text>
            </TouchableOpacity>

          </View>
        </View>
      </Modal>

    </View>
  );
}
