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
});
