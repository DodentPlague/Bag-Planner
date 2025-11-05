import { Ionicons } from '@expo/vector-icons';
import { useState } from "react";
import { ScrollView, Text, TouchableOpacity, View } from 'react-native';
import styles from '../constants/Style';
import { useAppState } from '../constants/UserState';
import BudgetCard from '../ui/BudgetCard';


export default function HomeScreen() {
    const { name, setName, money, setMoney, budgets, setBudgets } = useAppState();
    const [cards, setCards] = useState([]);

    const addCard = () => {
        setCards([
            ...cards,
            { title: "New Budget", allocated: 0, used: 0 }
        ]);
    };


    return (
        <View style={styles.container}>
            <ScrollView contentContainerStyle={styles.scrollContainer}>
                <View style={styles.header}>
                    <Text style={styles.welcome}>Welcome, Tim!</Text>
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
                    <Text style={styles.balance}>$1,234,567.89</Text>
                </View>

                <View style={styles.sectionHeader}>
                    <Text style={styles.sectionTitle}>Your Budgets:</Text>
                    <TouchableOpacity onPress={addCard}>
                        <Ionicons name="add-circle-outline" size={24} color="#555" />
                    </TouchableOpacity>
                </View>

                {cards.map((card, index) => (
                    <BudgetCard
                        key={index}
                        title={card.title}
                        allocated={card.allocated}
                        used={card.used}
                    />
                ))}
            </ScrollView>

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
