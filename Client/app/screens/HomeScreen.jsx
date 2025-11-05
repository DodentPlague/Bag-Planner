import { Ionicons } from '@expo/vector-icons';
import { useState } from 'react';
import { ScrollView, Text, TouchableOpacity, View } from 'react-native';
import styles from '../constants/Style';
import { useAppState } from '../constants/UserState';
import AddBudget from '../ui/AddBudget';
import BudgetCard from '../ui/BudgetCard';

export default function HomeScreen() {
    const { name, money, budgets, setBudgets } = useAppState();
    const [showModal, setShowModal] = useState(false);

    const addBudgetCard = (title, allocated) => {
        setBudgets([
        ...budgets,
        { title, allocated, used: 0 }
        ]);
    };


    return (
        <View style={styles.container}>
            <ScrollView contentContainerStyle={styles.scrollContainer}>
                <View style={styles.header}>
                    <Text style={styles.welcome}>Welcome, {name}!</Text>
                    <TouchableOpacity style={styles.iconButton}>
                        <Ionicons name="globe-outline" size={24} color="#555" />
                    </TouchableOpacity>
                </View>

                <View style={styles.card}>
                    <View style={styles.cardHeader}>
                        <Text style={styles.cardTitle}>your balance:</Text>
                        <TouchableOpacity>
                            <Ionicons name="add-circle-outline" size={24} color="#555" />
                        </TouchableOpacity>
                    </View>
                    <Text style={styles.balance}>${money.toLocaleString()}</Text>
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
                        title={budget.title}
                        allocated={budget.allocated}
                        used={budget.used}
                    />
                ))}
            </ScrollView>

            <AddBudget
                visible={showModal}
                onClose={() => setShowModal(false)}
                onSubmit={addBudgetCard}
            />

            <View style={styles.navbar}>
                <TouchableOpacity style={styles.navItem}>
                    <Ionicons name="bag-outline" size={24} color="#555" />
                    <Text style={styles.navLabel}>Your Bag</Text>
                </TouchableOpacity>
                <TouchableOpacity style={styles.navItem}>
                    <Ionicons name="settings-outline" size={24} color="#555" />
                    <Text style={styles.navLabel}>Settings</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
}
