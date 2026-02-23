import { Ionicons } from '@expo/vector-icons';
import { useState } from 'react';
import { ScrollView, Text, TouchableOpacity, View, Modal, TextInput } from 'react-native';
import styles from '../constants/Style';
import { useAppState } from '../constants/UserState';
import AddBudget from '../ui/AddBudget';
import BudgetCard from '../ui/BudgetCard';
import { useNavigation } from '@react-navigation/native';

export default function HomeScreen() {
    const navigation = useNavigation();

    const { 
        token, 
        usernameDisplay, 
        budgets, 
        balanceDollars, 
        balanceCents,
        loadUserState
    } = useAppState();

    const [showModal, setShowModal] = useState(false);

    // Add Balance Modal
    const [showAddBalanceModal, setShowAddBalanceModal] = useState(false);
    const [amountInput, setAmountInput] = useState("");
    const [accountInput, setAccountInput] = useState("");

    const SERVER_URL = "http://localhost:8080";

    const formatMoney = () => {
        return `${balanceDollars}.${balanceCents.toString().padStart(2, "0")}`;
    };

    const addBudgetCard = async (title, allocated) => {
        try {
            const dollars = Math.floor(allocated);
            const cents = Math.round((allocated - dollars) * 100);

            await fetch(`${SERVER_URL}/CreateBudget`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    token: token,
                    name: title,
                    dollars: dollars,
                    cents: cents
                }),
            });

            await loadUserState(token);

        } catch (err) {
            console.log("Failed to create budget:", err);
        }
    };

    // Add Balance Handler
    const handleAddBalance = async () => {
        const numeric = Number(amountInput);
        if (isNaN(numeric)) return;

        const dollars = Math.floor(numeric);
        const cents = Math.round((numeric - dollars) * 100);

        try {
            await fetch(`${SERVER_URL}/Transfer`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    token: token,
                    accountNumber: Number(accountInput),
                    dollars: dollars,
                    cents: cents
                }),
            });

            setShowAddBalanceModal(false);
            setAmountInput("");
            setAccountInput("");
            await loadUserState(token);

        } catch (err) {
            console.log("Failed to add balance:", err);
        }
    };

    return (
        <View style={styles.container}>
            <ScrollView contentContainerStyle={styles.scrollContainer}>
                <View style={styles.header}>
                    <Text style={styles.welcome}>Welcome, {usernameDisplay}!</Text>
                    <TouchableOpacity style={styles.iconButton}>
                        <Ionicons name="globe-outline" size={24} color="#555" />
                    </TouchableOpacity>
                </View>

                <View style={styles.card}>
                    <View style={styles.cardHeader}>
                        <Text style={styles.cardTitle}>your balance:</Text>

                        <TouchableOpacity onPress={() => setShowAddBalanceModal(true)}>
                            <Ionicons name="add-circle-outline" size={24} color="#555" />
                        </TouchableOpacity>
                    </View>

                    <Text style={styles.balance}>${formatMoney()}</Text>
                </View>

                <View style={styles.sectionHeader}>
                    <Text style={styles.sectionTitle}>Your Budgets:</Text>
                    <TouchableOpacity onPress={() => setShowModal(true)}>
                        <Ionicons name="add-circle-outline" size={24} color="#555" />
                    </TouchableOpacity>
                </View>

                {budgets.map((budget, index) => (
                    <BudgetCard
                        key={index}
                        title={budget.name}
                        allocated={`${budget.allocated_dollars}.${budget.allocated_cents.toString().padStart(2, "0")}`}
                        used={`${budget.used_dollars}.${budget.used_cents.toString().padStart(2, "0")}`}
                    />
                ))}
            </ScrollView>

            <AddBudget
                visible={showModal}
                onClose={() => setShowModal(false)}
                onSubmit={addBudgetCard}
            />

            {/* Add Balance Modal */}
            <Modal transparent visible={showAddBalanceModal} animationType="slide">
                <View style={styles.modalOverlay}>
                    <View style={styles.modalContainer}>
                        <Text style={styles.modalTitle}>Add Balance</Text>

                        <TextInput
                            placeholder="Account Number"
                            value={accountInput}
                            onChangeText={setAccountInput}
                            keyboardType="numeric"
                            style={styles.input}
                        />

                        <TextInput
                            placeholder="Amount"
                            value={amountInput}
                            onChangeText={setAmountInput}
                            keyboardType="numeric"
                            style={styles.input}
                        />

                        <View style={styles.modalButtons}>
                            <TouchableOpacity onPress={() => setShowAddBalanceModal(false)} style={styles.modalCancel}>
                                <Text>Cancel</Text>
                            </TouchableOpacity>

                            <TouchableOpacity onPress={handleAddBalance} style={styles.modalAdd}>
                                <Text>Add</Text>
                            </TouchableOpacity>
                        </View>
                    </View>
                </View>
            </Modal>

            {/* Bottom Navbar */}
            <View style={styles.navbar}>

                <TouchableOpacity style={styles.navItem}>
                    <Ionicons name="bag-outline" size={24} color="#555" />
                    <Text style={styles.navLabel}>Your Bag</Text>
                </TouchableOpacity>
                
                <TouchableOpacity 
                    style={styles.navItem} 
                    onPress={() => navigation.navigate("Connections")}
                >
                    <Ionicons name="people-outline" size={24} color="#555" />
                    <Text style={styles.navLabel}>Connections</Text>
                </TouchableOpacity>

                <TouchableOpacity style={styles.navItem}>
                    <Ionicons name="settings-outline" size={24} color="#555" />
                    <Text style={styles.navLabel}>Settings</Text>
                </TouchableOpacity>

            </View>
        </View>
    );
}
