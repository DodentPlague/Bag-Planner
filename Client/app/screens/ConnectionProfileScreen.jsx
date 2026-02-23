import { View, Text, TouchableOpacity, Modal, TextInput, ScrollView } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useNavigation, useRoute } from "@react-navigation/native";
import { useAppState } from "../constants/UserState";
import styles from "../constants/Style";
import { useState } from "react";

export default function ConnectionProfileScreen() {
  const navigation = useNavigation();
  const route = useRoute();

  const { username } = route.params;
  const {
    token,
    refreshUserState,
    usernameDisplay,
    transferHistory,
    saveTransferLocally,
  } = useAppState();

  const [showPayModal, setShowPayModal] = useState(false);
  const [amount, setAmount] = useState("");

  function parseAmount(input) {
    const cleaned = input.replace("$", "").trim();

    if (!cleaned.includes(".")) {
      return {
        dollars: parseInt(cleaned || "0"),
        cents: 0,
      };
    }

    const [d, c] = cleaned.split(".");
    return {
      dollars: parseInt(d || "0"),
      cents: parseInt((c || "0").padEnd(2, "0").slice(0, 2)),
    };
  }

  async function handlePayConfirm() {
    if (amount.trim() === "") {
      alert("Enter an amount");
      return;
    }

    const { dollars, cents } = parseAmount(amount);
    if (isNaN(dollars) || isNaN(cents)) {
      alert("Invalid amount");
      return;
    }

    let res = await fetch("http://localhost:8080/PayUser", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        token: token,
        recipientUsername: username,
        dollars,
        cents,
      }),
    });

    if (res.status !== 200) {
      alert("Payment failed.");
      return;
    }

    // Save locally
    await saveTransferLocally({
      from: usernameDisplay,
      to: username,
      dollars,
      cents,
    });

    alert("Payment successful.");
    setShowPayModal(false);
    setAmount("");

    await refreshUserState();
  }

  // Filter history between these two users
  const betweenUs = transferHistory.filter(
    (t) =>
      (t.from === usernameDisplay && t.to === username) ||
      (t.from === username && t.to === usernameDisplay)
  );

  return (
    <View style={styles.profileContainer}>
      {/* Back button */}
      <TouchableOpacity
        style={styles.profileBackBtn}
        onPress={() => navigation.goBack()}
      >
        <Ionicons name="arrow-back" size={28} color="#000" />
      </TouchableOpacity>

      {/* User Circle */}
      <View style={styles.profileCircle}>
        <Text style={styles.profileInitial}>{username[0].toUpperCase()}</Text>
      </View>

      {/* Pay button */}
      <TouchableOpacity
        style={styles.profilePayBtn}
        onPress={() => setShowPayModal(true)}
      >
        <Text style={styles.profilePayText}>Pay</Text>
      </TouchableOpacity>

      {/* Header */}
      <Text style={styles.profilePaymentsTitle}>Payments:</Text>

      {/* Payment History */}
      <View style={{ width: "85%", marginTop: 20 }}>
        {betweenUs.length === 0 ? (
          <Text style={styles.historyEmptyText}>
            No payment history between you and this user.
          </Text>
        ) : (
          betweenUs
            .sort((a, b) => b.id - a.id)
            .map((t) => {
              const received = t.to === usernameDisplay;
              return (
                <View key={t.id} style={styles.historyEntry}>
                  <View>
                    <Text style={styles.historyLabel}>
                      {received ? "Received:" : "Sent:"}
                    </Text>
                    <Text style={styles.historyDate}>
                      {new Date(t.timestamp).toLocaleDateString()}
                    </Text>
                  </View>

                  <Text
                    style={[
                      styles.historyAmountStyled,
                      { color: received ? "blue" : "red" },
                    ]}
                  >
                    {received ? "+" : "-"}${t.dollars}.
                    {t.cents.toString().padStart(2, "0")}
                  </Text>
                </View>
              );
            })
        )}
      </View>


      {/* Pay Modal */}
      <Modal visible={showPayModal} transparent animationType="slide">
        <View style={styles.connectionsModalOverlay}>
          <View style={styles.connectionsModalBox}>
            <Text style={styles.connectionsModalTitle}>Enter Amount</Text>

            <TextInput
              placeholder="$0.00"
              keyboardType="numeric"
              value={amount}
              onChangeText={setAmount}
              style={styles.connectionsModalInput}
            />

            <TouchableOpacity
              style={styles.connectionsModalAddBtn}
              onPress={handlePayConfirm}
            >
              <Text style={styles.connectionsModalAddText}>Send</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.connectionsModalCancelBtn}
              onPress={() => {
                setShowPayModal(false);
                setAmount("");
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
