# 🎫 Application Web de Suivi des Tickets IT

---

## 🏫 Informations Générales

**Université Ibn Zohr – Faculté des Sciences d’Agadir**  
**Centre d’Excellence IT – Master d’Excellence en Ingénierie Logicielle**  
**Encadré par : Pr. Jaafar IDRAIS**  
**Réalisé par : Aya ABOUNACER, Hiba RAIS, Nizar RAHMOUN, Zineb SAIDI**  
**Année universitaire : 2024–2025**

---

## 🧠 Présentation du Projet

L'**Application Web de Suivi des Tickets IT** est une application web développée pour **la gestion et le suivi des tickets de support informatique**.  
Elle permet aux utilisateurs de signaler leurs problèmes techniques, aux administrateurs d’attribuer les tickets aux techniciens, et aux techniciens de traiter et mettre à jour leur avancement.  

Cette plateforme facilite la **communication**, la **traçabilité** et la **gestion centralisée** des incidents informatiques au sein d’une organisation.

---

## 🎯 Objectifs Réalisés

- 🎟️ Création, consultation et suivi des tickets  
- 👩‍💼 Attribution des tickets par les administrateurs aux techniciens  
- 🧰 Traitement et mise à jour du statut des tickets  
- 📧 Notification automatique par e-mail lors des événements clés  
- 📜 Historique des tickets et des actions effectuées  
- 🔐 Gestion des rôles et des accès : **User**, **Technician**, **Admin**  
- 🐳 Déploiement de l’application via **Docker**

---

## ⚙️ Technologies Utilisées

| Composant | Technologie | Description |
|------------|-------------|-------------|
| 🧩 **Backend** | Spring Boot, EJB, JPA | Logique métier et persistance |
| 🌐 **Frontend** | JSP, HTML5, CSS3, Bootstrap | Interface utilisateur responsive |
| 🗄️ **Base de données** | MySQL | Gestion des données persistantes |
| 🔐 **Sécurité** | Spring Security | Authentification et autorisation |
| 📬 **E-mails** | JavaMail API | Notifications automatiques |
| 🐳 **Docker** | Containerisation et déploiement |
| 🧱 **Architecture** | MVC | Séparation Modèle / Vue / Contrôleur |

---

## 🌍 Accès et Exécution

### 🔧 Prérequis
- **Java 17+**
- **Docker Desktop**
- **Git**

### 🚀 Lancement du Projet

```bash
# Cloner le dépôt
https://github.com/aya-abounacer/Systeme_Suivi_Tikets.git
cd SRD-Systeme-Reparti-Demandes

# Lancer les conteneurs Docker
docker-compose up --build
```

---

## 🌐 Accès

- **Application :** [http://localhost:8080](http://localhost:8080)  
- **Base de données :** `localhost:3306` *(configurée dans docker-compose.yml)*

---

## 🧱 Fonctionnalités

### 👤 Utilisateur
- Création et consultation de ses tickets  
- Réception de notifications e-mail  
- Accès à son historique personnel  

### 🧰 Technicien
- Consultation des tickets assignés  
- Mise à jour du statut (`Open`, `In Progress`, `Resolved`)  
- Ajout de commentaires et suivi des interventions  

### 👩‍💼 Administrateur
- Gestion des utilisateurs (ajout, suppression, rôles)  
- Attribution des tickets aux techniciens  
- Visualisation de l’état global des tickets  

---

## 🧩 Architecture du Système

L’application suit une architecture **3-tiers** :  
- **Présentation (Vue)** : JSP + Bootstrap  
- **Logique métier (Contrôleur)** : Spring Boot + EJB  
- **Données (Modèle)** : MySQL via JPA  

---

## 🗄️ Modèle de Données

Le modèle relationnel comprend les tables suivantes :  

<img width="966" height="757" alt="Schéma de la base de données" src="https://github.com/user-attachments/assets/547eb0b3-4329-4b0f-ba24-485978617612" />

---

## 🧭 Diagrammes UML

### 🔹 Diagramme de Cas d’Utilisation
<img width="987" height="613" alt="Diagramme de cas d’utilisation" src="https://github.com/user-attachments/assets/09f72de5-9ab0-41a0-9b82-ffc6a85b4362" />

### 🔹 Diagramme de Classes
<img width="1039" height="596" alt="Diagramme de classes" src="https://github.com/user-attachments/assets/3adfd2b9-55b7-48bf-85cb-d63be0b6c2a4" />

---

## 💻 Interfaces Réalisées

### 🔐 Login
<img width="453" height="567" alt="Page de connexion" src="https://github.com/user-attachments/assets/f56e26ed-f79d-4d11-a66f-dca3ff4f777a" />

### 📝 Register
<img width="399" height="683" alt="Page d'inscription" src="https://github.com/user-attachments/assets/6763c43c-e179-4853-acdb-7f67b774096f" />

### 🧭 Dashboard Admin
<img width="1426" height="661" alt="Dashboard admin" src="https://github.com/user-attachments/assets/c3b59d19-b7f2-4d60-9b5c-c1a7c9e25d65" />
<img width="1451" height="658" alt="Dashboard admin suite" src="https://github.com/user-attachments/assets/081df945-38fa-4148-b296-f70e6466fc32" />

### 👥 Page de gestion des utilisateurs
<img width="1426" height="690" alt="Gestion des utilisateurs" src="https://github.com/user-attachments/assets/16b0ea27-262b-4015-9982-2c817119bed5" />

### 🗂️ Page de gestion des tickets
<img width="1038" height="497" alt="Gestion des tickets" src="https://github.com/user-attachments/assets/6491517e-e688-4fa0-a109-07cceaa374ba" />

### 👤 Dashboard Utilisateur
<img width="1034" height="467" alt="Dashboard utilisateur" src="https://github.com/user-attachments/assets/21aae3dc-ba6c-4628-92fe-3beb23ea4602" />

### 📝 Page des tickets utilisateur
<img width="1029" height="474" alt="Tickets utilisateur" src="https://github.com/user-attachments/assets/8ae8b647-bf42-45a3-b595-ffae29baab75" />

### 🔧 Dashboard Technicien
<img width="1037" height="476" alt="Dashboard technicien" src="https://github.com/user-attachments/assets/2b0f365b-0718-4231-8a35-b3502617c579" />

### 🧾 Tickets Technicien
<img width="1034" height="474" alt="Tickets technicien" src="https://github.com/user-attachments/assets/2816cad6-c4a4-49f7-91c1-e62b471e06c2" />

---

## 🧠 Points Techniques Réalisés

- Développement modulaire : séparation des packages (`controller`, `model`, `service`, `repository`)  
- Communication entre couches via **Spring Beans** et **EJB**  
- Gestion des rôles et sécurité via **Spring Security**  
- Containerisation via **Docker Compose** :  
  - `backend` (Spring Boot App)  
  - `db` (MySQL Container)  
- Envoi automatique d’e-mails à chaque mise à jour de ticket  

---

## 🧭 Perspectives d’Amélioration

- 📊 Intégration d’un tableau de bord statistique pour les administrateurs  
- ⏱️ Suivi du temps de résolution (SLA)  
- 📈 Visualisation graphique du nombre de tickets par statut  
- 🧾 Export des rapports en PDF ou Excel  
- ⚙️ Migration du frontend vers **React.js** pour une meilleure expérience utilisateur  

---

## 🎓 Contexte Académique

Projet réalisé dans le cadre du module  
**Systèmes Répartis et Distribués**  
du **Master d’Excellence en Ingénierie Logicielle** à  
**l’Université Ibn Zohr – Centre d’Excellence IT (Agadir)**.  

