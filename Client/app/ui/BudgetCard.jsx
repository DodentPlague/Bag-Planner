import { Text, View } from 'react-native';
import styles from '../constants/Style';

export default function BudgetCard({ title, allocated, used }) {
  return (
    <View style={styles.budgetCard}>
      <View style={styles.budgetHeader}>
        <Text style={styles.budgetTitle}>{title}</Text>
      </View>

      <View style={styles.budgetRow}>
        <Text style={styles.budgetText}>allocated funds:</Text>
        <Text style={styles.budgetValue}>{allocated}</Text>
      </View>

      <View style={styles.budgetRow}>
        <Text style={styles.budgetText}>funds used:</Text>
        <Text style={styles.budgetValue}>{used}</Text>
      </View>
    </View>
  );
}
