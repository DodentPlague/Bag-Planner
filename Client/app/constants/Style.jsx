import { StyleSheet } from 'react-native';

export default StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F8F8F8',
  },
  scrollContainer: {
    padding: 20,
    paddingBottom: 100,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 20,
  },
  welcome: {
    fontSize: 24,
    fontWeight: 'bold',
  },
  iconButton: {
    backgroundColor: '#eee',
    borderRadius: 20,
    padding: 6,
  },
  card: {
    backgroundColor: '#D6E4D7',
    borderRadius: 20,
    padding: 20,
    marginBottom: 20,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: '500',
  },
  balance: {
    fontSize: 30,
    fontWeight: 'bold',
    marginTop: 10,
  },
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 10,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: '600',
  },
  budgetCard: {
    backgroundColor: '#D6E4D7',
    borderRadius: 20,
    padding: 20,
    marginBottom: 15,
  },
  budgetHeader: {
    marginBottom: 10,
  },
  budgetTitle: {
    fontSize: 18,
    fontWeight: '600',
  },
  budgetRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  budgetText: {
    fontSize: 16,
    color: '#555',
  },
  budgetValue: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  navbar: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    flexDirection: 'row',
    justifyContent: 'space-around',
    backgroundColor: '#fff',
    paddingVertical: 12,
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    elevation: 5,
  },
  navItem: {
    alignItems: 'center',
  },
  navLabel: {
    fontSize: 12,
    color: '#555',
  },
  modalOverlay: {
  flex: 1,
  justifyContent: "center",
  alignItems: "center",
  backgroundColor: "rgba(0,0,0,0.5)"
},
modalContainer: {
  width: "80%",
  padding: 20,
  backgroundColor: "#fff",
  borderRadius: 10
},
modalTitle: {
  fontSize: 18,
  fontWeight: "bold",
  marginBottom: 10
},
input: {
  borderWidth: 1,
  borderColor: "#ccc",
  borderRadius: 6,
  padding: 10,
  marginVertical: 8
},
modalButtons: {
  flexDirection: "row",
  justifyContent: "space-between",
  marginTop: 10
},
modalCancel: {
  padding: 10
},
modalAdd: {
  padding: 10
},
overlay: {
  flex: 1,
  backgroundColor: "#F5F7F8",
  justifyContent: "space-between",
  paddingVertical: 30,
  paddingHorizontal: 20,
},
logoContainer: {
  marginTop: 40,
  alignItems: "center",
},
logo: {
  width: 120,
  height: 120,
},
formContainer: {
  marginTop: 10,
},
title: {
  fontSize: 26,
  fontWeight: "700",
  textAlign: "center",
  marginBottom: 22,
},
input: {
  borderWidth: 1,
  borderColor: "#BDBDBD",
  padding: 12,
  borderRadius: 10,
  marginBottom: 12,
  fontSize: 16,
},
footer: {
  marginBottom: 30,
},
authButton: {
  backgroundColor: "#27AE60",
  paddingVertical: 15,
  borderRadius: 12,
  alignItems: "center",
  marginBottom: 12,
},
authButtonText: {
  color: "white",
  fontWeight: "bold",
  fontSize: 20,
  textTransform: "uppercase",
  letterSpacing: 1,
},
switchText: {
  textAlign: "center",
  color: "#0066CC",
  fontSize: 15,
},
connectionsContainer: {
  flex: 1,
  backgroundColor: "#D6EED0"
},

connectionsBackBtn: {
  padding: 10,
  marginTop: 40,
  marginLeft: 10
},

connectionsSearch: {
  backgroundColor: "#EAEAEA",
  marginHorizontal: 20,
  marginTop: 10,
  padding: 10,
  borderRadius: 10
},

connectionsList: {
  padding: 20
},

connectionsSectionTitle: {
  fontSize: 18,
  fontWeight: "600",
  marginTop: 20,
  marginBottom: 10
},

connectionsRow: {
  flexDirection: "row",
  gap: 15,
  marginBottom: 15
},

connectionsItem: {
  alignItems: "center"
},

connectionsCircle: {
  width: 50,
  height: 50,
  borderRadius: 25,
  backgroundColor: "#fff",
  justifyContent: "center",
  alignItems: "center",
  borderWidth: 2,
  borderColor: "#000"
},

connectionsInitial: {
  fontSize: 20,
  fontWeight: "bold"
},

connectionsEntry: {
  flexDirection: "row",
  alignItems: "center",
  justifyContent: "space-between",
  marginVertical: 10
},

connectionsCircleLarge: {
  width: 55,
  height: 55,
  borderRadius: 27.5,
  backgroundColor: "#fff",
  justifyContent: "center",
  alignItems: "center",
  borderWidth: 2,
  borderColor: "#000"
},

connectionsInitialLarge: {
  fontSize: 22,
  fontWeight: "bold"
},

connectionsArrowBtn: {
  marginRight: 15
},
connectionsEmptyText: {
  textAlign: "center",
  marginTop: 20,
  fontSize: 18,
  color: "#888",
},
connectionsPageTitle: {
  fontSize: 28,
  fontWeight: "600",
  marginTop: 20,
  marginBottom: 15,
  marginLeft: 20,
},

connectionsAddBtn: {
  position: "absolute",
  top: 50,
  right: 20,
  zIndex: 5,
  backgroundColor: "#fff",
  borderRadius: 30,
  padding: 5,
  elevation: 4,
},

connectionsModalOverlay: {
  flex: 1,
  backgroundColor: "rgba(0,0,0,0.4)",
  justifyContent: "center",
  alignItems: "center",
},

connectionsModalBox: {
  width: "80%",
  backgroundColor: "#fff",
  padding: 20,
  borderRadius: 12,
  alignItems: "center",
},

connectionsModalTitle: {
  fontSize: 22,
  fontWeight: "600",
  marginBottom: 15,
},

connectionsModalInput: {
  width: "100%",
  padding: 10,
  borderWidth: 1,
  borderColor: "#ccc",
  borderRadius: 8,
  marginBottom: 10,
  fontSize: 16,
},

connectionsModalError: {
  color: "red",
  marginBottom: 10,
},

connectionsModalAddBtn: {
  backgroundColor: "#4A9BFF",
  paddingVertical: 10,
  paddingHorizontal: 20,
  borderRadius: 8,
  marginBottom: 10,
},

connectionsModalAddText: {
  color: "#fff",
  fontSize: 18,
  fontWeight: "bold",
},

connectionsModalCancelBtn: {
  paddingVertical: 8,
  paddingHorizontal: 20,
},

connectionsModalCancelText: {
  color: "#555",
  fontSize: 16,
},profileContainer: {
  flex: 1,
  backgroundColor: "#DFF3D8",
  alignItems: "center",
  paddingTop: 50,
},

profileBackBtn: {
  position: "absolute",
  top: 40,
  left: 20,
  zIndex: 10,
},

profileCircle: {
  width: 150,
  height: 150,
  borderRadius: 100,
  backgroundColor: "#FFF",
  justifyContent: "center",
  alignItems: "center",
  marginTop: 40,
  borderWidth: 2,
  borderColor: "#000",
},

profileInitial: {
  fontSize: 80,
  fontWeight: "600",
},

profilePayBtn: {
  backgroundColor: "#FFF",
  paddingVertical: 10,
  paddingHorizontal: 60,
  borderRadius: 20,
  marginTop: 20,
  elevation: 4,
},

profilePayText: {
  fontSize: 22,
  fontWeight: "600",
},

profilePaymentsTitle: {
  fontSize: 20,
  fontWeight: "600",
  marginTop: 30,
},

profileNotImplemented: {
  marginTop: 10,
  fontSize: 16,
  color: "#555",
},
historyCard: {
  backgroundColor: "#FFF",
  width: "90%",
  padding: 15,
  borderRadius: 15,
  marginTop: 20,
  elevation: 3,
},

historyScroll: {
  maxHeight: 250,
  marginTop: 10,
},

historyRow: {
  borderBottomWidth: 1,
  borderBottomColor: "#DDD",
  paddingVertical: 10,
},

historyRowLast: {
  paddingVertical: 10,
},

historyDirection: {
  fontSize: 16,
  fontWeight: "600",
},

historyAmount: {
  fontSize: 16,
  fontWeight: "bold",
  marginTop: 2,
},

historyTimestamp: {
  fontSize: 14,
  color: "#666",
  marginTop: 2,
},
historyEntry: {
  width: "100%",
  flexDirection: "row",
  justifyContent: "space-between",
  alignItems: "center",
  marginBottom: 20,
},

historyLabel: {
  fontSize: 16,
  fontWeight: "600",
},

historyDate: {
  fontSize: 15,
  color: "#333",
  marginTop: 2,
},

historyAmountStyled: {
  fontSize: 20,
  fontWeight: "700",
},

historyEmptyText: {
  textAlign: "center",
  marginTop: 20,
  fontSize: 16,
  color: "#555",
},

});
