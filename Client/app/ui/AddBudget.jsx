import { useState } from 'react';
import { Modal, Text, TextInput, TouchableOpacity, View } from 'react-native';
import styles from '../constants/Style';

export default function AddBudget({ visible, onClose, onSubmit }) {
  const [title, setTitle] = useState("");
  const [allocated, setAllocated] = useState("");

  const handleSubmit = () => {
    if (!title.trim()) return;
    onSubmit(title, Number(allocated));
    setTitle("");
    setAllocated("");
    onClose();
  };

  return (
    <Modal transparent visible={visible} animationType="slide">
      <View style={styles.modalOverlay}>
        <View style={styles.modalContainer}>
          <Text style={styles.modalTitle}>New Budget</Text>

          <TextInput
            placeholder="Name"
            value={title}
            onChangeText={setTitle}
            style={styles.input}
          />

          <TextInput
            placeholder="Allocated Amount"
            value={allocated}
            onChangeText={setAllocated}
            keyboardType="numeric"
            style={styles.input}
          />

          <View style={styles.modalButtons}>
            <TouchableOpacity onPress={onClose} style={styles.modalCancel}>
              <Text>Cancel</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={handleSubmit} style={styles.modalAdd}>
              <Text>Add</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
}
