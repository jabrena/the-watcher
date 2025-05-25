// Configuration constants
const MIN_STARS_THRESHOLD = 500;

let allRepos = [];
let filteredRepos = [];
let allCategories = [];
let allLanguages = [];

// Helper function to get the effective category (prioritizing category_corrected)
function getEffectiveCategory(repo) {
    return (repo.category_corrected && repo.category_corrected.trim() !== '')
        ? repo.category_corrected
        : repo.category;
}

async function loadRepositories() {
    try {
        const response = await fetch('data.js');
        const repos = await response.json();

        // Filter repositories with minimum stars threshold and sort by stars (descending)
        allRepos = repos
            .filter(repo => parseInt(repo.stars) >= MIN_STARS_THRESHOLD)
            .sort((a, b) => parseInt(b.stars) - parseInt(a.stars));
        filteredRepos = [...allRepos];

        // Extract unique categories (prioritizing category_corrected)
        allCategories = [...new Set(repos.map(repo => getEffectiveCategory(repo)).filter(cat => cat && cat.trim() !== ''))].sort();
        populateCategoryFilter();

        // Extract unique languages
        allLanguages = [...new Set(repos.map(repo => repo.language).filter(lang => lang && lang.trim() !== ''))].sort();
        populateLanguageFilter();

        displayRepositories();
        updateStats();

        document.getElementById('loading').style.display = 'none';
        document.getElementById('reposGrid').style.display = 'grid';
    } catch (error) {
        console.error('Error loading repositories:', error);
        document.getElementById('loading').innerHTML = '❌ Error loading repositories. Please check if the JSON file exists.';
    }
}

function displayRepositories() {
    const grid = document.getElementById('reposGrid');
    const noResults = document.getElementById('noResults');

    if (filteredRepos.length === 0) {
        grid.style.display = 'none';
        noResults.style.display = 'block';
        return;
    }

    grid.style.display = 'grid';
    noResults.style.display = 'none';

    grid.innerHTML = filteredRepos.map((repo, index) => {
        const repoName = repo.url.split('/').slice(-2).join('/');

        return `
            <div class="repo-card fade-in" onclick="openRepository('${repo.url}')" style="animation-delay: ${index * 0.05}s">
                <div class="repo-header">
                    <div class="repo-name">${repoName}</div>
                    <div class="repo-stars">${parseInt(repo.stars).toLocaleString()}</div>
                </div>
                <div class="repo-description">${repo.description || 'No description available'}</div>
                <div class="repo-meta">
                    <div style="display: flex; align-items: center; flex-wrap: wrap;">
                        <div class="repo-language">${repo.language}</div>
                        ${getEffectiveCategory(repo) ? `<div class="repo-category">${getEffectiveCategory(repo)}</div>` : ''}
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function updateStats() {
    const totalRepos = allRepos.length;
    const totalStars = allRepos.reduce((sum, repo) => sum + parseInt(repo.stars), 0);
    const avgStars = Math.round(totalStars / totalRepos);

    document.getElementById('totalRepos').textContent = totalRepos.toLocaleString();
    document.getElementById('totalStars').textContent = totalStars.toLocaleString();
    document.getElementById('avgStars').textContent = avgStars.toLocaleString();
}

function openRepository(url) {
    window.open(url, '_blank');
}

function populateCategoryFilter() {
    const categoryFilter = document.getElementById('categoryFilter');

    // Clear existing options except the first one
    categoryFilter.innerHTML = '<option value="">📂 All Categories</option>';

    // Add category options
    allCategories.forEach(category => {
        const option = document.createElement('option');
        option.value = category;
        option.textContent = category;
        categoryFilter.appendChild(option);
    });
}

function populateLanguageFilter() {
    const languageFilter = document.getElementById('languageFilter');

    // Clear existing options except the first one
    languageFilter.innerHTML = '<option value="">☕ All Languages</option>';

    // Add language options
    allLanguages.forEach(language => {
        const option = document.createElement('option');
        option.value = language;
        option.textContent = language;
        languageFilter.appendChild(option);
    });
}

function filterRepositories() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const selectedCategory = document.getElementById('categoryFilter').value;
    const selectedLanguage = document.getElementById('languageFilter').value;

    filteredRepos = allRepos.filter(repo => {
        // Text search filter
        const repoName = repo.url.split('/').slice(-2).join('/').toLowerCase();
        const description = (repo.description || '').toLowerCase();
        const language = repo.language.toLowerCase();
        const effectiveCategory = getEffectiveCategory(repo);
        const category = (effectiveCategory || '').toLowerCase();

        const matchesSearch = searchTerm === '' ||
            repoName.includes(searchTerm) ||
            description.includes(searchTerm) ||
            language.includes(searchTerm) ||
            category.includes(searchTerm);

        // Category filter
        const matchesCategory = selectedCategory === '' || effectiveCategory === selectedCategory;

        // Language filter
        const matchesLanguage = selectedLanguage === '' || repo.language === selectedLanguage;

        return matchesSearch && matchesCategory && matchesLanguage;
    });

    displayRepositories();
}

// Keep the old function name for backward compatibility
function searchRepositories() {
    filterRepositories();
}

// Event listeners
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('searchInput').addEventListener('input', filterRepositories);
    document.getElementById('categoryFilter').addEventListener('change', filterRepositories);
    document.getElementById('languageFilter').addEventListener('change', filterRepositories);

    // Load repositories when page loads
    loadRepositories();
});
